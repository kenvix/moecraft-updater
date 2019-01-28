//--------------------------------------------------
// Class Environment
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;

import net.moecraft.generator.jsonengine.CommonEngine;
import net.moecraft.generator.jsonengine.engine.BalthildEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import net.moecraft.generator.meta.GeneratorConfig;
import net.moecraft.generator.meta.scanner.FileScanner;
import net.moecraft.generator.updater.repo.DNSRepoManager;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private final static Class       repoManager      = DNSRepoManager.class;
    private final static String      dnsRepoDomain    = "updater-repo.moecraft.net";
    private final static String      appName          = "MoeCraft Toolbox";
    private static       Path        basePath;
    private static       Path        updaterPath;
    private static       Path        cachePath;

    static void loadEnvironment(CommandLine cmd) throws IOException {
        Environment.cmd = cmd;
        baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
        generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
        baseMoeCraftPath = baseMoeCraftDir.getCanonicalPath().replace('\\', '/');
        updateDescription = cmd.hasOption('i') ? cmd.getOptionValue('i') : "";
        updateVersion = cmd.hasOption('l') ? cmd.getOptionValue('l') : "1.0";
        basePath = Paths.get(".");
        updaterPath = basePath.resolve("Updater");
        cachePath = updaterPath.resolve("Cache");
    }

    public static Path getUpdaterPath() {
        return updaterPath;
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

    public static Class getRepoManager() {
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
}
