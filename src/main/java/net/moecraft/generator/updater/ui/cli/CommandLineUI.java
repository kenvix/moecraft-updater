//--------------------------------------------------
// Class CommandLineUI
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.cli;

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
import net.moecraft.generator.updater.ui.UpdaterUI;
import net.moecraft.generator.updater.update.FileDamagedException;
import net.moecraft.generator.updater.update.FileUpdateApplier;
import net.moecraft.generator.updater.update.UpdateComparer;
import net.moecraft.generator.updater.update.UpdateCriticalException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

/**
 * Command Line UI and basic of GraphicalUI
 */
public class CommandLineUI implements UpdaterUI {
    private Scanner scanner;

    protected void logln(String text) {
        System.out.println(text);
    }

    protected void log(String text) {
        System.out.print(text);
    }

    protected void logf(String format, Object ... args) {
        System.out.printf(format, args);
    }

    /**
     * Display command line UI.
     */
    @Override
    public void display() {
        scanner = new Scanner(System.in);

        try {
            showWelcomePage();
            Repo selectedRepo = showRepoSelectPage();

            MetaResult remoteResult = showUpdateGetMetaPage(selectedRepo, null);
            MetaResult localResult = showUpdateScanLocalPage();
            MetaResult compareResult = showUpdateComparePage(remoteResult, localResult);

            //ProgressBar progressBar = new ProgressBar("", 1000);
//            OnDownloadMissionReady onDownloadMissionReady = (mission, downloadManager, fileNode) -> {
//                //progressBar.setExtraMessage(String.format("%.2fMB", (float) fileNode.getSize() / 1024 / 1024));
//            };
//            OnDownloadProgressChanged onDownloadProgressChanged = (mission, fileNode) -> {
//                //int progress = 1000 * (int) ((double) mission.getDownloadedSize() / (double) fileNode.getSize());
//                //progressBar.stepTo(progress);
//                //progressBar.setExtraMessage(String.format("%s | %.2fMB", mission.getReadableSpeed(), (float) fileNode.getSize() / 1024 / 1024));
//            };
//            OnDownloadMissionFinished onDownloadMissionFinished = (downloadStatus, mission, fileNode) -> {
//
//            };
//            OnDownloadMissionFailed onDownloadMissionFailed = (failNum, fileNode, exception) -> {
//                if(failNum < Environment.getDownloadMaxTries()) {
//                    return true;
//                } else {
//                    flagDownloadFailed = true;
//                    return false;
//                }
//            };
            showUpdateDownloadPage(compareResult, selectedRepo);
            showUpdateMergePage(compareResult);
            showUpdateApplyPage(compareResult);
            showUpdateCleanCachePage();
        } catch (UpdateCriticalException ex) {
            logln("更新失败：严重错误：" + ex.getMessage());
            System.exit(ex.getExitCode());
        }
    }

    final private void showUpdateCleanCachePage() {
        printNormalBorderLine();
        logln("正在清理缓存 ...");
        try {
            FileUtils.deleteDirectory(Environment.getCachePath().toFile());
        } catch (Exception ex) {
            Environment.getLogger().info("Clean cache failed: " + ex.getMessage());
        }
    }

    final protected void showUpdateApplyPage(MetaResult compareResult) throws UpdateCriticalException {
        printNormalBorderLine();
        logln("正在应用更新");
        FileUpdateApplier updateApplier = new FileUpdateApplier(compareResult);
        updateApplier.start();
    }

    final protected void showUpdateMergePage(MetaResult compareResult) throws UpdateCriticalException {
        printNormalBorderLine();
        logln("正在合并文件对象 ...");

        try {
            if(!Environment.getUpdaterObjectPath().toFile().exists())
                FileUtils.forceMkdir(Environment.getUpdaterObjectPath().toFile());

            for (Map.Entry<String, List<FileNode>> objectList :  compareResult.getGlobalObjects().entrySet()) {
                logln("正在合并: " + objectList.getKey());
                ObjectEngine.mergeObject(objectList.getKey(), objectList.getValue());
            }

        } catch (IOException ex) {
            throw new UpdateCriticalException("合并文件对象失败：" + ex.getMessage(), 78);
        }
    }

    final protected void showUpdateDownloadPage(MetaResult compareResult, Repo repo) throws UpdateCriticalException {
        printNormalBorderLine();
        logln("正在下载需要更新的文件 ...");

        final RepoNetworkUtil networkUtil = new RepoNetworkUtil(repo);

        if (!Environment.getCachePath().toFile().exists()) {
            if (!Environment.getCachePath().toFile().mkdirs())
                throw new UpdateCriticalException("无法创建缓存文件夹", 73);
        }

        for (Map.Entry<String, List<FileNode>> objectList :  compareResult.getGlobalObjects().entrySet()) {
            for (FileNode object : objectList.getValue()) {
                Path savePath = Environment.getCachePath().resolve(object.getFile().getName());
                logln("正在下载: " + object.getPath());
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
                        logf("下载失败，正在重试 (%d/%d 次): %s -> %s\n", failNum+1, Environment.getDownloadMaxTries(), ex.getMessage(), object.getPath());
                    }
                }
                if(failNum == Environment.getDownloadMaxTries())
                    throw new UpdateCriticalException("无法下载新文件，请检查您的网络", 75);
            }
        }

//        compareResult.getGlobalObjects().forEach((objectMd5Key, objectList) -> {
//            networkUtil.downloadObjects(objectList, onDownloadProgressChanged, onDownloadMissionFailed, onDownloadMissionFinished, onReady);
//        });
    }

    final protected MetaResult showUpdateComparePage(MetaResult remoteResult, MetaResult localResult) {
        printNormalBorderLine();
        logln("正在比较本地和远端文件，请稍候 ...");

        UpdateComparer updateComparer = new UpdateComparer(remoteResult, localResult);
        return updateComparer.compare();
    }

    final protected MetaResult showUpdateScanLocalPage() {
        printNormalBorderLine();
        logln("正在扫描本地文件，请稍候 ...");

        MetaScanner metaScanner = new MetaScanner(new FileScanner());
        return metaScanner.scan();
    }

    final protected Repo[] showWelcomePage() throws UpdateCriticalException {
        printBoldBorderLine();

        logln("欢迎使用 MoeCraft 客户端更新器");
        logln("正在获取更新节点列表，请稍候 ...");

        Repo[] repos = Environment.getRepos();
        if(repos == null)
            throw new UpdateCriticalException("错误：无法获取更新节点，请检查您的网络", 7);

        return repos;
    }

    final protected Repo showRepoSelectPage() {
        printBoldBorderLine();
        Repo[] repos = Environment.getRepos();

        logln("警告: 该程序将于它所在的文件夹安装 MoeCraft 客户端, 并删除该文件夹内的其他 Minecraft 版本. 请勿把安装器与无关文件放在同一文件夹内, 否则, 使用者需自行承担可能发生的数据损失.");
        logln("注意: 如果你需要添加自定义 Mod, 请在本程序所在目录下建立 Mods 文件夹(注意大小写), 并把你的 Mod 放入这个文件夹中. 不要把 Mod 直接放在 .minecraft/mods 中, 否则它们会被删除.");

        printNormalBorderLine();
        logln("请选择一个下载源 (输入序号)：");

        Arrays.stream(repos).forEach(repo -> logf("[%d] %s\n", repo.getOrder(), repo.getDescription()));
        log("请输入下载源的序号: ");

        int repoID;
        Repo selectedRepo;
        while (true) {
            try {
                repoID = scanner.nextInt();
                selectedRepo = repos[repoID];
                break;
            } catch (InputMismatchException ex) {
                scanner.next();
                log("输入的内容无效，请重新输入下载源的序号: ");
            } catch (IndexOutOfBoundsException ex) {
                log("输入的序号无效，请重新输入下载源的序号: ");
            }
        }
        logln("使用节点：" + selectedRepo.getDescription());

        return selectedRepo;
    }

    final protected MetaResult showUpdateGetMetaPage(Repo repo, @Nullable Consumer<IOException> onDownloadFailed) throws UpdateCriticalException {
        ParserEngine parserEngine = new NewMoeEngine();
        RepoNetworkUtil networkUtil = new RepoNetworkUtil(repo);
        MetaResult remoteResult = null;

        printNormalBorderLine();
        logf("正在下载更新信息(%s) ...\n", repo.getMetaFileName());

        for (int i = 0; i < Environment.getDownloadMaxTries(); i++) {
            try {
                String remoteJSONData = networkUtil.downloadRepoMetaAsString();
                remoteResult = parserEngine.decode(remoteJSONData);
                break;
            } catch (IOException ex) {
                if(onDownloadFailed != null)
                    onDownloadFailed.accept(ex);

                logf("尝试下载更新信息时出错 (第 %d/%d 次尝试): %s\n", i+1, Environment.getDownloadMaxTries(), ex.getMessage());
            }
        }

        if(remoteResult == null)
            throw new UpdateCriticalException("无法下载更新信息，更新失败。请检查您的网络", 71);

        return remoteResult;
    }

    private void printBoldBorderLine() {
        System.out.println("=========================================================");
    }

    private void printNormalBorderLine() {
        System.out.println("---------------------------------------------------------");
    }

    private void pause() {

    }
}
