//--------------------------------------------------
// Class UserFileRegister
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.Environment;
import org.apache.commons.io.FileUtils;

import java.io.File;
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
            if (!file.isDirectory()) {
                Path linkFile = minecraftModsDirectoryPath.resolve(file.getName());

                if (!linkFile.toFile().exists())
                    FileHandler.linkCompatible(file.toPath(), linkFile, FileHandler.LinkType.Hard);
            }
        });
    }

    public static void createUserModsDir() {
        DirectoryHandler.create(Environment.getUserModsPath());
    }
}
