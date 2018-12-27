//--------------------------------------------------
// Class Environment
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;

import net.moecraft.generator.jsonengine.CommonEngine;
import net.moecraft.generator.jsonengine.engine.BalthildEngine;
import net.moecraft.generator.jsonengine.engine.NewMoeEngine;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

public final class Environment {

    private final static Class[] generatorEngines = {BalthildEngine.class, NewMoeEngine.class};
    private static       File    baseMoeCraftDir;
    private final static Class[] parserEngines    = {NewMoeEngine.class};
    private static       File    generatorConfigFile;
    private static       String  baseMoeCraftPath;

    static void loadEnvironmentByCommandLine(CommandLine cmd) throws IOException {
        baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
        generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
        baseMoeCraftPath = baseMoeCraftDir.getCanonicalPath().replace('\\', '/');
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

}
