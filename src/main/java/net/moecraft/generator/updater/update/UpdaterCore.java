//--------------------------------------------------
// Class UpdaterCore
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.repo.RepoNetworkUtil;
import net.moecraft.generator.updater.update.events.DownloadMetaEvent;
import net.moecraft.generator.updater.update.events.DownloadMetaEventType;
import net.moecraft.generator.updater.update.events.UpdateChildEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Observable;
import java.util.function.Consumer;

public class UpdaterCore extends Observable {
    private UpdateStage stage = UpdateStage.Prepare;

    public void start() throws UpdateCriticalException {

    }

    public void resume(UpdateStage fromStage) throws UpdateCriticalException {

    }

    public final MetaResult startScanLocalFileStage() {
        setStage(UpdateStage.ScanLocalFile);

        notifyObservers("正在扫描本地文件");
        MetaScanner metaScanner = new MetaScanner(new FileScanner());
        MetaResult result = metaScanner.scan();

        notifyObservers(result);
        return result;
    }

    public final MetaResult startDownloadMetaStage(Repo repo, @Nullable Consumer<IOException> onDownloadFailed) throws UpdateCriticalException {
        setStage(UpdateStage.DownloadMeta);

        ParserEngine parserEngine = new NewMoeEngine();
        RepoNetworkUtil networkUtil = new RepoNetworkUtil(repo);
        MetaResult remoteResult = null;

        notifyObservers(String.format("正在下载更新信息(%s) ...\n", repo.getMetaFileName()), new DownloadMetaEvent(DownloadMetaEventType.Downloading));

        for (int i = 0; i < Environment.getDownloadMaxTries(); i++) {
            try {
                String remoteJSONData = networkUtil.downloadRepoMetaAsString();
                remoteResult = parserEngine.decode(remoteJSONData);
                break;
            } catch (IOException ex) {
                if(onDownloadFailed != null)
                    onDownloadFailed.accept(ex);

                notifyObservers(String.format("尝试下载更新信息时出错 (第 %d/%d 次尝试): %s\n", i+1, Environment.getDownloadMaxTries(), ex.getMessage()),
                        new DownloadMetaEvent(DownloadMetaEventType.FailedButRedownloading, i+1), ex);
            }
        }

        if(remoteResult == null) {
            UpdateCriticalException ex = new UpdateCriticalException("无法下载更新信息，更新失败。请检查您的网络", 71);

            notifyObservers(ex.getMessage(),
                    new DownloadMetaEvent(DownloadMetaEventType.Failed, Environment.getDownloadMaxTries()), ex);

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

    public void notifyObservers(Object result) {
        notifyObservers(new UpdateEvent(getStage(), result));
    }
}
