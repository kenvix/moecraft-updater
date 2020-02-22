//--------------------------------------------------
// Class Environment
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;

import net.moecraft.generator.jsonengine.engine.BalthildEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.GeneratorConfig;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.*;
import net.moecraft.generator.updater.ui.cli.CommandLineUI;
import net.moecraft.generator.updater.ui.gui.FXGraphicalUI;
import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Environment {

    private static CommandLine cmd;
    private final static Class[] generatorEngines = {NewMoeEngine.class};
    private static File baseMoeCraftDir;
    private final static Class[] parserEngines = {NewMoeEngine.class};
    private static File generatorConfigFile;
    private static String baseMoeCraftPath;
    private static String updateDescription;
    private static String updateVersion;
    private final static Class metaScanner = FileScanner.class;
    private final static Class[] repoManager = {AccountCenterRepoManager.class, DNSRepoManager.class, LocalIntegratedRepoManager.class};
    private final static String dnsRepoDomain = "updater-repo.moecraft.net";
    private static final String repoManagerURL = "https://user.moecraft.net:8443/API/Updater/repo";
    private final static String appName = "MoeCraft Toolbox";
    private static Class uiProvider;
    private static Repo[] repos;
    private final static int downloadMaxTries = 5;
    private final static int dnsMaxTries = 20;
    private static Logger logger;
    private static boolean isUpdater;
    private static Path basePath;
    private static Path deployPath;
    private static Path updaterPath;
    private static Path cachePath;
    private static Path updaterObjectPath;
    private static Path userModsPath;
    private static boolean isConsoleWindowExists;
    private static boolean isRunningOnWindowsPlatform;
    private static int jvmPid = -1;

    static void loadEnvironment(CommandLine cmd) throws IOException {
        Environment.cmd = cmd;

        uiProvider = cmd.hasOption("cli") ? CommandLineUI.class : FXGraphicalUI.class;

        baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
        generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
        baseMoeCraftPath = baseMoeCraftDir.getCanonicalPath().replace('\\', '/');
        updateDescription = cmd.hasOption('i') ? cmd.getOptionValue('i') : "";
        isUpdater = !cmd.hasOption('g');
        updateVersion = cmd.hasOption('l') ? cmd.getOptionValue('l') : "1.0";

        basePath = Paths.get(".");
        updaterPath = basePath.resolve("Updater");
        cachePath = updaterPath.resolve("Cache");
        deployPath = basePath.resolve("Deployment");
        updaterObjectPath = updaterPath.resolve("Objects");
        userModsPath = updaterPath.resolve("Mods");
        isConsoleWindowExists = System.console() != null;
        isRunningOnWindowsPlatform = System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    public static int getDnsMaxTries() {
        return dnsMaxTries;
    }

    public static int getDownloadMaxTries() {
        return downloadMaxTries;
    }

    public static boolean isUpdater() {
        return isUpdater;
    }

    @NotNull
    public static Logger getLogger() {
        if (logger == null) {
            synchronized (Environment.class) {
                if (logger != null)
                    return logger;

                logger = Logger.getGlobal();
            }
        }
        return logger;
    }

    public static String getColoredString(String str, Level level) {
        if (level == Level.FINER || level == Level.FINEST)
            return "\033[32m" + str + "\033[0m";

        else if (level == Level.FINE)
            return "\033[1;32m" + str + "\033[0m";

        else if (level == Level.INFO)
            return "\033[1;36m" + str + "\033[0m";

        else if (level == Level.WARNING)
            return "\033[1;33m" + str + "\033[0m";

        else if (level == Level.SEVERE)
            return "\033[1;31m" + str + "\033[0m";

        else if (level == Level.CONFIG)
            return "\033[35m" + str + "\033[0m";
        else
            return str;
    }

    private static Map<String, String> simplifiedSourceClassNameMap = new HashMap<>();
    public static String getSimplifiedSourceClassName(String sourceClassName) {
        if (simplifiedSourceClassNameMap.containsKey(sourceClassName))
            return simplifiedSourceClassNameMap.get(sourceClassName);

        String result = getShortPackageName(sourceClassName);
        simplifiedSourceClassNameMap.put(sourceClassName, result);
        return result;
    }

    public static String getShortPackageName(String sourceClassName) {
        StringBuilder builder = new StringBuilder(sourceClassName);

        if (sourceClassName.contains(".")) {
            int beginPosition = 0;
            int nextPosition = 0;

            do {
                nextPosition = builder.indexOf(".", beginPosition + 1);
                int deletedLength = nextPosition - beginPosition - 1;
                builder.delete(beginPosition + 1, nextPosition);
                beginPosition = nextPosition - deletedLength + 1;
            } while ((nextPosition = builder.indexOf(".", beginPosition + 1)) >= 0);
        }

        return builder.toString();
    }

    public static Path getUpdaterPath() {
        return updaterPath;
    }

    public static String getRepoManagerURL() {
        return repoManagerURL;
    }

    public static boolean isIsConsoleWindowExists() {
        return isConsoleWindowExists;
    }

    public static Path getCachePath() {
        return cachePath;
    }

    public static Path getBasePath() {
        return basePath;
    }

    public static Class getUiProvider() {
        return uiProvider;
    }

    public static Path getUpdaterObjectPath() {
        return updaterObjectPath;
    }

    public static Class getMetaScanner() {
        return metaScanner;
    }

    public static Class[] getParserEngines() {
        return parserEngines;
    }

    public static Class[] getGeneratorEngines() {
        return generatorEngines;
    }

    public static File getBaseMoeCraftDir() {
        return baseMoeCraftDir;
    }

    public static File getGeneratorConfigFile() {
        return generatorConfigFile;
    }

    public static String getBaseMoeCraftPath() {
        return baseMoeCraftPath;
    }

    public static String getUpdateVersion() {
        return updateVersion;
    }

    public static void setUpdateVersion(String updateVersion) {
        Environment.updateVersion = updateVersion;
    }

    public static String getUpdateDescription() {
        return updateDescription;
    }

    public static void setUpdateDescription(String updateDescription) {
        Environment.updateDescription = updateDescription;
    }

    public static String getDnsRepoDomain() {
        return dnsRepoDomain;
    }

    @Deprecated
    public static String getOutJsonName() {
        return GeneratorConfig.getInstance().getOutputJsonName();
    }

    public static Path getDeployPath() {
        return deployPath;
    }

    public static Class[] getRepoManager() {
        return repoManager;
    }

    public static boolean isGeneratorMode() {
        return cmd.hasOption('g');
    }

    public static CommandLine getCommandLine() {
        return cmd;
    }

    public static String getAppName() {
        return appName;
    }

    public static Repo[] getRepos() {
        if (repos == null) {
            Class[] repoManagers = Environment.getRepoManager();
            for (Class repoManager : repoManagers) {
                try {
                    return repos = ((RepoManager) repoManager.newInstance()).getRepos();
                } catch (Exception ex) {
                    Environment.getLogger().warning("Repo manager " + repoManager.getSimpleName() + " Failed! Fallback...");
                }
            }
        }
        return repos;
    }

    public static boolean isRunningOnWindowsPlatform() {
        return isRunningOnWindowsPlatform;
    }

    public static void setRepos(Repo[] repos) {
        Environment.repos = repos;
    }

    public static Path getUserModsPath() {
        return userModsPath;
    }

    public static void showErrorMessage(String message) {
        if (!isConsoleWindowExists) {
            JOptionPane.showMessageDialog(null, message, "错误", JOptionPane.ERROR_MESSAGE);
        }

        System.err.println(message);
    }

    /**
     * Get JVM PID
     *
     * @return int JVM PID
     * @throws UnsupportedOperationException Getting PID is not support on current JVM
     */
    @SuppressWarnings("all")
    public static int getJvmPid() {
        if (jvmPid != -1)
            return jvmPid;

        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        try {
            java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);

            sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
            java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
            pid_method.setAccessible(true);

            return jvmPid = (Integer) pid_method.invoke(mgmt);
        } catch (Throwable ignored1) {
            //Fallback
            String jvmName = runtime.getName();
            String pidString = jvmName.split("@")[0];

            if (pidString == null || pidString.isEmpty())
                throw new UnsupportedOperationException();

            try {
                return jvmPid = Integer.parseInt(pidString);
            } catch (NumberFormatException exception) {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static String getJvmPath(boolean useJavaW) {
        if (isRunningOnWindowsPlatform)
            return System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + (useJavaW ? "javaw.exe" : "java.exe");
        else
            return System.getProperties().getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    public static String getJvmPath() {
        return getJvmPath(false);
    }
}
