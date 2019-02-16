//--------------------------------------------------
// Class UpdaterCore
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.ObjectEngine;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.repo.RepoNetworkUtil;
import net.moecraft.generator.updater.update.events.*;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.function.Consumer;

public class UpdaterCore extends Observable {
    public static final UpdateStage[] updateOrder = new UpdateStage[] {
            UpdateStage.DownloadMeta,
            UpdateStage.ScanLocalFile,
            UpdateStage.Compare,
            UpdateStage.DownloadNewFile,
            UpdateStage.MergeObject,
            UpdateStage.ApplyNewFile,
            UpdateStage.RegisterUserMod,
            UpdateStage.CleanCache,
            UpdateStage.Finish
    };

    private UpdateStage stage = UpdateStage.Prepare;
    private Repo repo;

    public UpdaterCore(Repo repo) {
        this.repo = repo;
    }

    public void start() throws UpdateCriticalException {
        resume(0);
    }

    public void resume(int stageIndex, Object... initVars) throws UpdateCriticalException {
        MetaResult remoteResult = null;
        MetaResult localResult = null;
        MetaResult compareResult = null;

        for (int i = stageIndex; i < updateOrder.length; i++) {
            switch (updateOrder[i]) {
                case DownloadMeta:
                    remoteResult = startDownloadMetaStage(null);
                    break;

                case ScanLocalFile:
                    localResult = startScanLocalFileStage();
                    break;

                case Compare:
                    compareResult = startCompareStage(remoteResult == null ? remoteResult = (MetaResult) initVars[0] : remoteResult,
                            localResult == null ? localResult = (MetaResult) initVars[1] : localResult);
                    break;

                case DownloadNewFile:
                    startDownloadNewFileStage(compareResult == null ? compareResult = (MetaResult) initVars[0]: compareResult);
                    break;

                case MergeObject:
                    startMergeObjectStage(compareResult == null ? compareResult = (MetaResult) initVars[0]: compareResult);
                    break;

                case ApplyNewFile:
                    startApplyNewFileStage(compareResult == null ? compareResult = (MetaResult) initVars[0]: compareResult);
                    break;

                case RegisterUserMod:
                    startRegisterUserModStage();
                    break;

                case CleanCache:
                    startCleanCacheStage();
                    break;

                case Finish:
                    startFinishStage(remoteResult == null ? remoteResult = (MetaResult) initVars[0] : remoteResult);
                    break;
            }
        }
    }

    public void resume(UpdateStage fromStage, Object... initVars) throws UpdateCriticalException {
        resume(Arrays.binarySearch(updateOrder, fromStage), initVars);
    }

    public void startFinishStage(MetaResult remoteResult) {
        setStage(UpdateStage.Finish);
        notifyObservers(remoteResult);
    }

    public void startCleanCacheStage() {
        setStage(UpdateStage.CleanCache);
        notifyObservers("正在清理缓存 ...", SimpleResultType.Started);

        try {
            System.gc();
            FileUtils.deleteDirectory(Environment.getCachePath().toFile());
        } catch (Exception ex) {
            notifyObservers("Clean cache failed: " + ex.getMessage(), ex);
        }

        notifyObservers("清理缓存完成");
    }

    public void startRegisterUserModStage() {
        setStage(UpdateStage.RegisterUserMod);
        notifyObservers("正在注册用户自定义 Mods ...", SimpleResultType.Started);

        UserFileRegister.registerUserMods();
        notifyObservers("注册自定义 Mod 完成");
    }

    public void startApplyNewFileStage(MetaResult compareResult) throws UpdateCriticalException {
        setStage(UpdateStage.ApplyNewFile);
        notifyObservers("正在应用更新 ...", compareResult, SimpleResultType.Started);

        FileUpdateApplier updateApplier = new FileUpdateApplier(compareResult);
        updateApplier.start();

        notifyObservers(compareResult);
    }

    public void startMergeObjectStage(MetaResult compareResult) throws UpdateCriticalException {
        setStage(UpdateStage.MergeObject);
        notifyObservers("正在合并文件对象 ...", compareResult, SimpleResultType.Started);

        try {
            if(!Environment.getUpdaterObjectPath().toFile().exists())
                FileUtils.forceMkdir(Environment.getUpdaterObjectPath().toFile());

            for (Map.Entry<String, List<FileNode>> objectList :  compareResult.getGlobalObjects().entrySet()) {
                notifyObservers("正在合并: " + objectList.getKey(), objectList, SimpleResultType.Loading);
                ObjectEngine.mergeObject(objectList.getKey(), objectList.getValue());
            }

        } catch (IOException ex) {
            UpdateCriticalException exception = new UpdateCriticalException("合并文件对象失败：" + ex.getMessage(), 78);
            notifyObservers(ex.getMessage(), exception);
            throw exception;
        }
    }

    public void startDownloadNewFileStage(MetaResult compareResult) throws UpdateCriticalException {
        setStage(UpdateStage.DownloadNewFile);
        notifyObservers("正在下载需要更新的文件 ...", compareResult, SimpleResultType.Started);

        try {
            final RepoNetworkUtil networkUtil = new RepoNetworkUtil(repo);

            notifyObservers("创建缓存文件夹", new DownloadNewFileEvent(DownloadEventType.Downloading, DownloadEventAction.CreateDirectory));
            if (!Environment.getCachePath().toFile().exists()) {
                if (!Environment.getCachePath().toFile().mkdirs())
                    throw new UpdateCriticalException("无法创建缓存文件夹", 73);
            }

            for (Map.Entry<String, List<FileNode>> objectList :  compareResult.getGlobalObjects().entrySet()) {
                for (FileNode object : objectList.getValue()) {
                    notifyObservers("准备下载: " + object.getPath(), new DownloadNewFileEvent(DownloadEventType.Downloading, DownloadEventAction.FileIsCached));

                    Path savePath = Environment.getCachePath().resolve(object.getFile().getName());
                    boolean hasCached= false;

                    if(savePath.toFile().exists()) {
                        String cacheFileMd5 = FileTool.getFileMD5(savePath.toFile());
                        if(cacheFileMd5 != null && cacheFileMd5.equals(object.getExpectedMd5()))
                            hasCached = true;
                    }

                    if(hasCached) {
                        notifyObservers("已缓存的下载: " + object.getPath(), new DownloadNewFileEvent(DownloadEventType.Downloading, DownloadEventAction.FileIsCached));
                    } else {
                        notifyObservers("正在下载: " + object.getPath(), new DownloadNewFileEvent(DownloadEventType.Downloading, DownloadEventAction.Downloading));

                        int failNum = 0;
                        for(; failNum < Environment.getDownloadMaxTries(); failNum++) {
                            try {
                                networkUtil.simpleDownloadFile(networkUtil.getRepoFileURL(object), savePath);

                                String downloadedFileMd5 = FileTool.getFileMD5(savePath.toFile());

                                if(downloadedFileMd5 == null || !downloadedFileMd5.equals(object.getExpectedMd5()))
                                    throw new FileDamagedException(String.format("下载的文件已损坏 ( 下载的文件: %s，服务器上的文件：%s", downloadedFileMd5, object.getExpectedMd5()));
                                else
                                    break;
                            } catch (Exception ex) {
                                notifyObservers(String.format("下载失败，正在重试 (%d/%d 次): %s -> %s\n", failNum+1, Environment.getDownloadMaxTries(), ex.getMessage(), object.getPath()),
                                        new DownloadNewFileEvent(DownloadEventType.FailedButRedownloading, DownloadEventAction.DownloadFailed), ex);
                            }
                        }
                        if(failNum == Environment.getDownloadMaxTries())
                            throw new UpdateCriticalException("无法下载新文件，请检查您的网络", 75);
                    }
                }
            }
        } catch (UpdateCriticalException ex) {
            notifyObservers(ex.getMessage(), new DownloadNewFileEvent(DownloadEventType.Failed, DownloadEventAction.DownloadFailed), ex);
            throw ex;
        }
    }

    public final MetaResult startCompareStage(MetaResult remoteResult, MetaResult localResult) {
        setStage(UpdateStage.Compare);
        notifyObservers("正在比较本地和远端文件，请稍候 ...", SimpleResultType.Started);

        UpdateComparer updateComparer = new UpdateComparer(remoteResult, localResult);
        MetaResult result = updateComparer.compare();

        notifyObservers(result);
        return result;
    }

    public final MetaResult startScanLocalFileStage() {
        setStage(UpdateStage.ScanLocalFile);
        notifyObservers("正在扫描本地文件", SimpleResultType.Started);

        MetaScanner metaScanner = new MetaScanner(new FileScanner());
        MetaResult result = metaScanner.scan();

        notifyObservers(result);
        return result;
    }

    public final MetaResult startDownloadMetaStage(@Nullable Consumer<IOException> onDownloadFailed) throws UpdateCriticalException {
        setStage(UpdateStage.DownloadMeta);

        ParserEngine parserEngine = new NewMoeEngine();
        RepoNetworkUtil networkUtil = new RepoNetworkUtil(repo);
        MetaResult remoteResult = null;

        notifyObservers(String.format("正在下载更新信息(%s) ...\n", repo.getMetaFileName()), new DownloadMetaEvent(DownloadEventType.Downloading));

        for (int i = 0; i < Environment.getDownloadMaxTries(); i++) {
            try {
                String remoteJSONData = networkUtil.downloadRepoMetaAsString();
                remoteResult = parserEngine.decode(remoteJSONData);
                break;
            } catch (IOException ex) {
                if(onDownloadFailed != null)
                    onDownloadFailed.accept(ex);

                notifyObservers(String.format("尝试下载更新信息时出错 (第 %d/%d 次尝试): %s\n", i+1, Environment.getDownloadMaxTries(), ex.getMessage()),
                        new DownloadMetaEvent(DownloadEventType.FailedButRedownloading, i+1), ex);
            }
        }

        if(remoteResult == null) {
            UpdateCriticalException ex = new UpdateCriticalException("无法下载更新信息，更新失败。请检查您的网络", 71);

            notifyObservers(ex.getMessage(),
                    new DownloadMetaEvent(DownloadEventType.Failed, Environment.getDownloadMaxTries()), ex);

            throw ex;
        }

        notifyObservers(remoteResult);
        return remoteResult;
    }

    public UpdateStage getStage() {
        return stage;
    }

    public UpdaterCore setStage(UpdateStage stage) {
        this.stage = stage;
        return this;
    }


    public void notifyObservers(UpdateEvent arg) {
        super.notifyObservers(arg);
        Environment.getLogger().finest(arg.getUpdateStage().toString() + " : " + arg.getDescription());
    }

    public void notifyObservers() {
        notifyObservers(new UpdateEvent(getStage()));
    }

    public void notifyObservers(String description, UpdateChildEvent childEvent) {
        notifyObservers(new UpdateEvent(getStage(), description, childEvent));
    }

    public void notifyObservers(String description, UpdateChildEvent childEvent, Exception ex) {
        notifyObservers(new UpdateEvent(getStage(), description, childEvent, ex));
    }

    public void notifyObservers(String description, SimpleResultType simpleResultType) {
        notifyObservers(new UpdateEvent(getStage(), description, simpleResultType));
    }

    public void notifyObservers(String description) {
        notifyObservers(new UpdateEvent(getStage(), description));
    }

    public void notifyObservers(String description, Object result) {
        notifyObservers(new UpdateEvent(getStage(), description, result));
    }

    public void notifyObservers(String description, Object result, SimpleResultType simpleResultType) {
        notifyObservers(new UpdateEvent(getStage(), description, result, simpleResultType));
    }

    public void notifyObservers(Object result) {
        notifyObservers(new UpdateEvent(getStage(), result));
    }
}
