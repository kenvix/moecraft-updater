//--------------------------------------------------
// Class net.moecraft.generator.Main
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.jsonengine.engine.BalthildEngine;
import net.moecraft.generator.meta.GeneratorConfig;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.scanner.FileScanner;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.out;

public class Main {


    public static void main(String[] args) {
        try {
            Logger.getGlobal().setLevel(Level.FINE);
            out.println(getHeader());
            Environment.loadEnvironmentByCommandLine(getCmd(args));
            File baseMoeCraftDir = Environment.getBaseMoeCraftDir();
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "MoeCraft root directory not found on '" + baseMoeCraftDir.getCanonicalPath() + "'. Please create a directory called 'MoeCraft' and run this program again.");
                System.exit(9);
            }
            String basePath = Environment.getBaseMoeCraftPath();
            Logger.getGlobal().log(Level.FINEST, "Current path: " + basePath);
            File generatorConfigFile = Environment.getGeneratorConfigFile();
            GeneratorConfig.getInstance(basePath, generatorConfigFile);
            MetaScanner scanner = new MetaScanner(new FileScanner());
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "generator_config.json not found on '" + generatorConfigFile.getCanonicalPath() + "'. Please specify where generator_config.json is and run this program again.");
                System.exit(8);
            }
            MetaResult     result             = scanner.scan();
            generateAll(result);
        } catch (MissingArgumentException ex) {
            out.println("Missing Argument: " + ex.getMessage());
        } catch (UnrecognizedOptionException ex) {
            out.println("Wrong Argument given: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unexpected Exception.");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateAll(MetaResult result) throws Exception {
        for (Class engine : Environment.getGeneratorEngines()) {
            if(!Modifier.isAbstract(engine.getModifiers())) {
                Logger.getGlobal().info("Generating result using " + engine.getSimpleName());
                GeneratorEngine instance       = (GeneratorEngine) engine.newInstance();
                String          generateResult = instance.encode(Environment.getBaseMoeCraftPath(), result);
                instance.save(Environment.getBaseMoeCraftPath(), generateResult);
                Logger.getGlobal().log(Level.FINE, "Write result formatted in " + engine.getSimpleName() + "  to " + Environment.getBaseMoeCraftPath());
            } else {
                Logger.getGlobal().info("Detected invalid generator engine: " + engine.getSimpleName());
            }
        }
    }

    private static CommandLine getCmd(String[] args) throws ParseException {
        Options ops = new Options();
        ops.addOption("p", "path",true, "Path to MoeCraft root directory. Default ./MoeCraft");
        ops.addOption("g", "generator",false, "Use Generator mode instead of updater mode.");
        ops.addOption("c", "config",true, "Path to generator_config.json. Default ./generator_config.json");
        ops.addOption("o", "output",false, "[Generator] Directory to put generated file. Default ./. Filename is defined by Generate Engine");
        ops.addOption("h", "help",false, "Print help messages");
        DefaultParser parser = new DefaultParser();
        CommandLine   cmd    = parser.parse(ops, args);
        if(cmd.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Generator", getHeader(), ops, "", true);
            System.exit(0);
        }
        return cmd;
    }

    private static String getHeader() {
        return "MoeCraft Updater Meta Generator // Written by Kenvix";
    }
}
