//--------------------------------------------------
// Class UserFileRegister
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.Environment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public class UserFileRegister {
    public static void registerUserMods() {
        String[] modExtensions = {"jar", "zip"};

        final Collection<File> modFiles = FileUtils.listFiles(Environment.getUserModsPath().toFile(), modExtensions, true);
        final Path minecraftModsDirectoryPath = Environment.getBaseMoeCraftDir().toPath().resolve(".minecraft").resolve("mods");

        if (!minecraftModsDirectoryPath.toFile().exists())
            DirectoryHandler.create(minecraftModsDirectoryPath);

        modFiles.forEach(file -> {
            try {
                if(!file.isDirectory()) {
                    Path linkFile = minecraftModsDirectoryPath.resolve(FileTool.getRelativePath(Environment.getUserModsPath().toAbsolutePath().toString(), file.getCanonicalPath()));

                    if(!linkFile.toFile().exists())
                        FileHandler.link(file.toPath(), linkFile, FileHandler.LinkType.Hard);
                }
            } catch (IOException ex) {
                Environment.getLogger().info("Link user mod failed: " + ex.getMessage());
            }
        });
    }

    public static void createUserModsDir() {
        DirectoryHandler.create(Environment.getUserModsPath());
    }
}
