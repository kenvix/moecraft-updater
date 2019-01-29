//--------------------------------------------------
// Class Environment
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;

import net.moecraft.generator.jsonengine.engine.BalthildEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.AccountCenterRepoManager;
import net.moecraft.generator.updater.repo.LocalIntegratedRepoManager;
import net.moecraft.generator.updater.repo.Repo;
import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class Environment {

    private static       CommandLine cmd;
    private final static Class[]     generatorEngines = {BalthildEngine.class, NewMoeEngine.class};
    private static       File        baseMoeCraftDir;
    private final static Class[]     parserEngines    = {NewMoeEngine.class};
    private static       File        generatorConfigFile;
    private static       String      baseMoeCraftPath;
    private static       String      updateDescription;
    private static       String      updateVersion;
    private final static Class       metaScanner      = FileScanner.class;
    private final static Class[]     repoManager      = {AccountCenterRepoManager.class, LocalIntegratedRepoManager.class};
    private final static String      dnsRepoDomain    = "updater-repo.moecraft.net";
    private static final String      repoManagerURL   = "https://accounts.moecraft.net/API/Updater/repo";
    private final static String      appName          = "MoeCraft Toolbox";
    private static       Repo[]      repos;
    private final static int         downloadMaxTries = 5;
    private final static int         dnsMaxTries      = 20;
    private static       Logger      logger;
    private static       boolean     isUpdater;
    private static       Path        basePath;
    private static       Path        updaterPath;
    private static       Path        cachePath;

    static void loadEnvironment(CommandLine cmd) throws IOException {
        Environment.cmd = cmd;
        baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
        generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
        baseMoeCraftPath = baseMoeCraftDir.getCanonicalPath().replace('\\', '/');
        updateDescription = cmd.hasOption('i') ? cmd.getOptionValue('i') : "";
        isUpdater = !cmd.hasOption('g');
        updateVersion = cmd.hasOption('l') ? cmd.getOptionValue('l') : "1.0";
        basePath = Paths.get(".");
        updaterPath = basePath.resolve("Updater");
        cachePath = updaterPath.resolve("Cache");
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
        if(logger == null) {
            synchronized (Environment.class) {
                if (logger != null)
                    return logger;

                logger = Logger.getGlobal();
            }
        }
        return logger;
    }

    public static Path getUpdaterPath() {
        return updaterPath;
    }

    public static String getRepoManagerURL() {
        return repoManagerURL;
    }


    public static Path getCachePath() {
        return cachePath;
    }

    public static Path getBasePath() {
        return basePath;
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
        return repos;
    }

    public static void setRepos(Repo[] repos) {
        Environment.repos = repos;
    }
}
