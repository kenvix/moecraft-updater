//--------------------------------------------------
// Class CommandLineUI
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.ui.cli;

import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.ui.UpdaterUI;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.System.out;

public final class CommandLineUI implements UpdaterUI {
    private Scanner scanner;

    @Override
    public void display() {
        scanner = new Scanner(System.in);

        showWelcomePage();
        Repo selectedRepo = showRepoSelectPage();
        showUpdatePage(selectedRepo);
    }

    private void showWelcomePage() {
        printBoldBorderLine();

        out.println("欢迎使用 MoeCraft 客户端更新器");
        out.println("正在获取更新节点列表，请稍候 ...");

        Repo[] repos = Environment.getRepos();
        if(repos == null) {
            out.println("错误：无法获取更新节点，请检查您的网络");
            System.exit(7);
        }
    }

    private Repo showRepoSelectPage() {
        printBoldBorderLine();
        Repo[] repos = Environment.getRepos();

        out.println("警告: 该程序将于它所在的文件夹安装 MoeCraft 客户端, 并删除该文件夹内的其他 Minecraft 版本. 请勿把安装器与无关文件放在同一文件夹内, 否则, 使用者需自行承担可能发生的数据损失.");
        out.println("注意: 如果你需要添加自定义 Mod, 请在本程序所在目录下建立 Mods 文件夹(注意大小写), 并把你的 Mod 放入这个文件夹中. 不要把 Mod 直接放在 .minecraft/mods 中, 否则它们会被删除.");

        printNormalBorderLine();
        out.println("请选择一个下载源 (输入序号)：");

        Arrays.stream(repos).forEach(repo -> out.printf("[%d] %s\n", repo.getOrder(), repo.getDescription()));
        out.print("请输入下载源的序号: ");

        int repoID;
        Repo selectedRepo;
        while (true) {
            try {
                repoID = scanner.nextInt();
                selectedRepo = repos[repoID];
                break;
            } catch (InputMismatchException ex) {
                scanner.next();
                out.print("输入的内容无效，请重新输入下载源的序号: ");
            } catch (IndexOutOfBoundsException ex) {
                out.print("输入的序号无效，请重新输入下载源的序号: ");
            }
        }
        out.println("使用节点：" + selectedRepo.getDescription());

        return selectedRepo;
    }

    private void showUpdatePage(Repo repo) {
        NewMoeEngine parserEngine = new NewMoeEngine();
    }

    private void printBoldBorderLine() {
        out.println("=========================================================");
    }

    private void printNormalBorderLine() {
        out.println("---------------------------------------------------------");
    }

    private void pause() {

    }
}
