//--------------------------------------------------
// Class DirectoryHandler
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class DirectoryHandler {

    public static void delete(Path directory) {
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException ex) {
            throw new UpdateCriticalException("Delete directory failed: " + directory.toString(), 80, ex);
        }
    }

    public static void create(Path directory) {
        try {
            if(!directory.toFile().exists())
                FileUtils.forceMkdir(directory.toFile());
        } catch (IOException ex) {
            throw new UpdateCriticalException("Create directory failed: " + directory.toString(), 80, ex);
        }
    }
}