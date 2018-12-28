//--------------------------------------------------
// Class FileScanner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta.scanner;

import net.moecraft.generator.meta.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileScanner implements CommonScanner {
    private final static GeneratorConfig config = GeneratorConfig.getInstance();

    public MetaResult scan(File dir, MetaNodeType type, MetaScanner in) {
        MetaResult    result = in.getResult();
        DirectoryNode directoryNode = result.addDirectoryNode(type, dir);
        Logger.getGlobal().log(Level.FINER, "Scanning directory " + dir.getPath());
        scan(dir, directoryNode, true);
        return result;
    }

    public DirectoryNode scan(File dir, DirectoryNode parentNode, boolean isRootDirectory) {
        DirectoryNode directoryNode = isRootDirectory ? parentNode : parentNode.addDirectoryNode(dir);
        File[] list = dir.listFiles();
        if(list != null) {
            for (File file : list) {
                if(file.isFile()) {
                    if(!config.isFileExcluded(file)) {
                        Logger.getGlobal().log(Level.FINEST, "+ File: " + file.getName());
                        directoryNode.addFileNode(file);
                    }
                } else {
                    if(!config.isDirectoryExcluded(file))
                        scan(file, directoryNode, false);
                }
            }
        } else {
            Logger.getGlobal().warning("Failed to open directory " + dir.getPath());
        }
        return directoryNode;
    }
}
