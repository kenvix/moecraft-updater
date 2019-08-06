package net.moecraft.generator;
//--------------------------------------------------
// Class net.moecraft.generator.Init
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.*;
import net.moecraft.generator.BuildConfig;

import static java.lang.System.out;

class Init {
    static void initSystem(String[] args) {
        try {
            Environment.getLogger().setLevel(Level.FINE);
            out.println(Init.getHeader());
            out.printf("Built at %s. Running at JDK %s\n", BuildConfig.BUILD_DATE.toString(), System.getProperty("java.version"));

            Environment.loadEnvironment(getCmd(args));
            File baseMoeCraftDir = Environment.getBaseMoeCraftDir();

            if (!baseMoeCraftDir.exists()) {
                try {
                    Environment.getLogger().log(Level.FINE, "MoeCraft root directory not found on '" + baseMoeCraftDir.getCanonicalPath() + "'. Create.");
                    FileUtils.forceMkdir(baseMoeCraftDir);
                } catch (Exception ex) {
                    Environment.getLogger().log(Level.SEVERE, "Create MoeCraft root directory FAILED '" + baseMoeCraftDir.getCanonicalPath() + "'.");
                    System.exit(9);
                }

            }
        } catch (MissingArgumentException ex) {
            Environment.showErrorMessage("Missing Argument: " + ex.getMessage());
            throw new RuntimeException(ex);
        } catch (UnrecognizedOptionException ex) {
            Environment.showErrorMessage("Wrong Argument given: " + ex.getMessage());
            throw new RuntimeException(ex);
        } catch (IOException | ParseException e ) {
            throw new RuntimeException(e);
        }
    }

    static CommandLine getCmd(String[] args) throws ParseException {
        Options ops = new Options();

        ops.addOption("p", "path", true, "Path to MoeCraft root directory. Default ./MoeCraft");
        ops.addOption("g", "generator", false, "Use Generator mode instead of updater mode.");
        ops.addOption("c", "config", true, "Path to generator_config.json. Default ./generator_config.json");
        ops.addOption("i", "description", true, "[Generator] Add a description for this update. Default for description generator_config.json");
        ops.addOption("l", "version", true, "[Generator] Version this update. Default for version in generator_config.json");
        ops.addOption("v", "verbose", false, "Verbose logging mode.");
        ops.addOption("cli", false, "Use Command Line User Interface.");
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

    static String getHeader() {
        return String.format("MoeCraft Updater Meta Generator Ver.%s/%d // Written by Kenvix", BuildConfig.VERSION, BuildConfig.VERSION_CODE);
    }
}
