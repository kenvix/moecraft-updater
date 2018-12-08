//--------------------------------------------------
// Class net.moecraft.generator.Main
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;
import net.moecraft.generator.jsondata.balthild.BalthildJsonData;
import net.moecraft.generator.meta.GeneratorConfig;
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
            CommandLine cmd = getCmd(args);
            File baseMoeCraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./MoeCraft");
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "MoeCraft root directory not found on '" + baseMoeCraftDir.getCanonicalPath() + "'. Please create a directory called 'MoeCraft' and run this program again.");
                System.exit(9);
            }
            String basePath = baseMoeCraftDir.getCanonicalPath();
            Logger.getGlobal().log(Level.FINEST, "Current path: " + basePath);

            File generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
            GeneratorConfig.getInstance(basePath, generatorConfigFile);
            MetaScanner scanner = new MetaScanner(new FileScanner());
            if(!baseMoeCraftDir.exists()) {
                Logger.getGlobal().log(Level.SEVERE, "generator_config.json not found on '" + generatorConfigFile.getCanonicalPath() + "'. Please specify where generator_config.json is and run this program again.");
                System.exit(8);
            }
            out.println((new BalthildJsonData()).encode(basePath, scanner.scan()));
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unexpected Exception.");
            ex.printStackTrace();
        }
        /*try {
            CommandLine cmd = getCmd(args);
            System.out.println(getHeader());
            if(cmd.hasOption("proxy-type")) {
                Properties sysProperties = System.getProperties();
                sysProperties.setProperty("proxySet", "true");
                switch (cmd.getOptionValue("proxy-type")) {
                    case "socks":
                    case "socks5":
                        System.setProperty("proxySet", "true");
                        System.setProperty("socksProxyHost", cmd.getOptionValue("proxy-host"));
                        System.setProperty("socksProxyPort", cmd.getOptionValue("proxy-port"));
                        System.out.println("With socks proxy " + System.getProperty("socksProxyHost") + ":" + System.getProperty("socksProxyPort"));
                        break;

                    case "http":
                        sysProperties.setProperty("proxySet", "true");
                        sysProperties.setProperty("http.proxyHost", cmd.getOptionValue("proxy-host"));
                        sysProperties.setProperty("http.proxyPort", cmd.getOptionValue("proxy-port"));
                        System.out.println("With http proxy " + System.getProperty("http.proxyHost") + ":" + System.getProperty("http.proxyPort"));
                        break;
                }
            }
            homepageURL = cmd.hasOption("u") ? cmd.getOptionValue("u") : "https://www.pixiv.net/";
            String[] enabledDrivers = cmd.getOptionValue("d").split(",");
            for (String enabledDriver: enabledDrivers) {
                if(registeredDrivers.)
            }
            (new Tasker<>(new ImageItemDownloader(cmd.getOptionValue("p")), Integer.parseInt(cmd.getOptionValue("j")))).start("Downloader");
        } catch (Exception ex) {
            System.err.println("ERROR: Unable to initialize!!!");
            System.err.println(ex.toString());
            System.exit(1);
        }*/

    }

    private static CommandLine getCmd(String[] args) throws ParseException {
        Options ops = new Options();
        ops.addOption("p", "path",true, "Path to MoeCraft root directory. Default ./MoeCraft");
        ops.addOption("c", "config",true, "Path to generator_config.json. Default ./generator_config.json");
        ops.addOption("h", "help",false, "Print help messages.");
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
