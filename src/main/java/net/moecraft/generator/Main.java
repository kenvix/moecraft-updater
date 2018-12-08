//--------------------------------------------------
// Class net.moecraft.generator.Main
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;
import net.moecraft.generator.jsondata.balthild.BalthildJsonData;
import net.moecraft.generator.meta.GeneratorConfig;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;
import net.moecraft.generator.meta.scanner.FileScanner;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) {
        try {
            Logger.getGlobal().setLevel(Level.FINE);
            CommandLine cmd = getCmd(args);
            File baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "MoeCraft root directory not found on '" + baseMoeCraftDir.getCanonicalPath() + "'. Please create a directory called 'MoeCraft' and run this program again.");
                System.exit(9);
            }
            String basePath = baseMoeCraftDir.getCanonicalPath().replace('\\','/');
            Logger.getGlobal().log(Level.FINEST, "Current path: " + basePath);

            File generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
            GeneratorConfig.getInstance(basePath, generatorConfigFile);
            MetaScanner scanner = new MetaScanner(new FileScanner());
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "generator_config.json not found on '" + generatorConfigFile.getCanonicalPath() + "'. Please specify where generator_config.json is and run this program again.");
                System.exit(8);
            }
            MetaResult result = scanner.scan();
            BalthildJsonData balthildJsonData = new BalthildJsonData();
            String balthildJsonResult = balthildJsonData.encode(basePath, result);
            balthildJsonData.save(basePath, balthildJsonResult);
            Logger.getGlobal().log(Level.FINE, "Write result formatted in BalthildJsonData to " + basePath);
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unexpected Exception.");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static CommandLine getCmd(String[] args) throws ParseException {
        Options ops = new Options();
        ops.addOption("p", "path",true, "Path to MoeCraft root directory. Default ./MoeCraft");
        ops.addOption("c", "config",true, "Path to generator_config.json. Default ./generator_config.json");
        ops.addOption("o", "output",false, "Directory to put generated file. Default ./. Filename is defined by Generate Engine");
        DefaultParser parser = new DefaultParser();
        CommandLine   cmd    = parser.parse(ops, args);
        if(cmd.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("pixivbg", getHeader(), ops, "", true);
            System.exit(0);
        }
        return cmd;
    }

    private static String getHeader() {
        return "MoeCraft Updater Meta Generator // Written by Kenvix";
    }
}
