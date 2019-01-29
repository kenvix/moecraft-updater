//--------------------------------------------------
// Class net.moecraft.generator.Main
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator;

import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.meta.*;
import net.moecraft.generator.updater.repo.Repo;
import net.moecraft.generator.updater.repo.RepoManager;
import net.moecraft.generator.updater.ui.UpdaterIndex;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.*;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        try {
            Environment.getLogger().setLevel(Level.FINE);
            out.println(getHeader());

            Environment.loadEnvironment(getCmd(args));
            File baseMoeCraftDir = Environment.getBaseMoeCraftDir();

            if (!baseMoeCraftDir.exists()) {
                Environment.getLogger().log(Level.INFO, "MoeCraft root directory not found on '" + baseMoeCraftDir.getCanonicalPath() + "'. Create.");

                if (!baseMoeCraftDir.mkdirs()) {
                    Environment.getLogger().log(Level.SEVERE, "Create MoeCraft root directory FAILED '" + baseMoeCraftDir.getCanonicalPath() + "'.");
                    System.exit(9);
                }
            }

            String basePath = Environment.getBaseMoeCraftPath();
            Environment.getLogger().log(Level.FINEST, "Current path: " + basePath);

            if (Environment.isUpdater())
                runAsUpdater();
            else
                runAsGenerator();

        } catch (MissingArgumentException ex) {
            out.println("Missing Argument: " + ex.getMessage());
        } catch (UnrecognizedOptionException ex) {
            out.println("Wrong Argument given: " + ex.getMessage());
        } catch (Exception ex) {
            Environment.getLogger().log(Level.SEVERE, "Unexpected Exception.");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void runAsGenerator() throws Exception {
        File generatorConfigFile = Environment.getGeneratorConfigFile();
        GeneratorConfig config = GeneratorConfig.getInstance(generatorConfigFile);
        MetaScanner scanner = new MetaScanner((CommonScanner) Environment.getMetaScanner().newInstance());

        if (!Environment.getBaseMoeCraftDir().exists()) {
            Environment.getLogger().log(Level.SEVERE, "generator_config.json not found on '" + generatorConfigFile.getCanonicalPath() + "'. Please specify where generator_config.json is and run this program again.");
            System.exit(8);
        }

        MetaResult result = scanner.scan();
        result.setDescription(Environment.getUpdateDescription().isEmpty() ? config.getDescription() : Environment.getUpdateDescription());
        result.setVersion(Environment.getUpdateVersion().isEmpty() ? config.getVersion() : Environment.getUpdateVersion());

        if (config.getObjectSize() > 0) {
            Environment.getLogger().info("Generating objects....");
            ObjectEngine objectEngine = new ObjectEngine(result);
            objectEngine.startMakeObjects();
        }

        generateAll(result);
    }

    private static void runAsUpdater() throws Exception {
        try {
            Environment.setRepos(getRepos());
        } catch (IOException ex) {
            Environment.getLogger().severe(ex.getMessage());
            System.exit(7);
        }
        //testParser(new NewMoeEngine(), result);
        Environment.getLogger().finest("Starting Updater UI Thread ...");
        new Thread(UpdaterIndex::display).start();
    }

    private static Repo[] getRepos() throws Exception {
        RepoManager repoManager = (RepoManager) Environment.getRepoManager().newInstance();
        return repoManager.getRepos();
    }

    private static void generateAll(MetaResult result) throws Exception {
        for (Class engine : Environment.getGeneratorEngines()) {
            if (!Modifier.isAbstract(engine.getModifiers())) {
                Environment.getLogger().info("Generating result using " + engine.getSimpleName());

                GeneratorEngine instance = (GeneratorEngine) engine.newInstance();
                String generateResult = instance.encode(result);
                instance.save(generateResult);
                Environment.getLogger().log(Level.FINE, "Write result formatted in " + engine.getSimpleName() + "  to " + Environment.getBaseMoeCraftPath());
            } else {
                Environment.getLogger().info("Detected invalid generator engine: " + engine.getSimpleName());
            }
        }
    }

    private static <T extends GeneratorEngine & ParserEngine> void testParser(T instance, MetaResult result) throws Exception {
        String generateResult1 = instance.encode(result);
        MetaResult decodeResult = instance.decode(generateResult1);
        String generateResult2 = instance.encode(decodeResult);
        out.println(generateResult1);
        out.println(generateResult2);
        out.println(generateResult2.equals(generateResult1));
    }

    private static CommandLine getCmd(String[] args) throws ParseException {
        Options ops = new Options();

        ops.addOption("p", "path", true, "Path to MoeCraft root directory. Default ./MoeCraft");
        ops.addOption("g", "generator", false, "Use Generator mode instead of updater mode.");
        ops.addOption("c", "config", true, "Path to generator_config.json. Default ./generator_config.json");
        ops.addOption("i", "description", true, "[Generator] Add a description for this update. Default for description generator_config.json");
        ops.addOption("l", "version", true, "[Generator] Version this update. Default for version in generator_config.json");
        ops.addOption("v", "verbose", false, "Verbose logging mode.");
        ops.addOption("h", "help", false, "Print help messages");

        DefaultParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(ops, args);

        if (cmd.hasOption('h')) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Generator", getHeader(), ops, "", true);
            System.exit(0);
        }

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%s] [%s] [%s=>%s] %s\n",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(record.getMillis())),
                        record.getLevel().toString(),
                        record.getSourceClassName(),
                        record.getSourceMethodName(),
                        record.getMessage()
                );
            }
        });

        if (cmd.hasOption('v')) {
            Environment.getLogger().setUseParentHandlers(false);
            Environment.getLogger().setLevel(Level.FINEST);
            consoleHandler.setLevel(Level.FINEST);
            Environment.getLogger().log(Level.FINEST, "Verbose logging mode enabled.");
        }

        Environment.getLogger().addHandler(consoleHandler);
        return cmd;
    }

    private static String getHeader() {
        return "MoeCraft Updater Meta Generator // Written by Kenvix";
    }
}
