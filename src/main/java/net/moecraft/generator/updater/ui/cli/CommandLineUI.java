//--------------------------------------------------
// Class CommandLineUI
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.cli;

import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.repo.RepoNetworkUtil;
import net.moecraft.generator.updater.ui.UpdaterUI;
import net.moecraft.generator.updater.update.UpdateComparer;
import net.moecraft.generator.updater.update.UpdateCriticalException;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

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

            MetaResult remoteResult = showUpdateGetMetaPage(selectedRepo);
            MetaResult localResult = showUpdateScanLocalPage();
            MetaResult compareResult = showUpdateComparePage(remoteResult, localResult);

            showUpdateDownloadPage(compareResult);
        } catch (UpdateCriticalException ex) {
            logln(ex.getMessage());
            System.exit(ex.getExitCode());
        }
    }

    final protected void showUpdateDownloadPage(MetaResult compareReault) {
        printNormalBorderLine();
        logln("正在下载需要更新的文件 ....");

        //compareReault
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

    final protected MetaResult showUpdateGetMetaPage(Repo repo) throws UpdateCriticalException {
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
                logf("尝试下载更新信息时出错 (第 %d/%d 次尝试): %s\n", i+1, Environment.getDownloadMaxTries(), ex.getMessage());
            }
        }

        if(remoteResult == null)
            throw new UpdateCriticalException("无法下载更新信息，更新失败。请检查您的网络", 71);

        return remoteResult;
    }

    private void printBoldBorderLine() {
        logln("=========================================================");
    }

    private void printNormalBorderLine() {
        logln("---------------------------------------------------------");
    }

    private void pause() {

    }
}
