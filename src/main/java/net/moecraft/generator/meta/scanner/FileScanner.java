//--------------------------------------------------
// Class FileScanner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta.scanner;

import net.moecraft.generator.meta.*;

import java.io.File;
import java.util.logging.Logger;

public class FileScanner implements Scanner {
    public MetaResult scan(File dir, MetaScanner in) {
        MetaResult    result = in.getResult();
        MetaNodeType  type = in.getMetaNodeType();
        DirectoryNode directoryNode = result.addDirectoryNode(type, dir);
        Logger.getGlobal().info("Scanning directory " + dir.getPath());
        result.addDirectoryNode(type, directoryNode);
        File[] list = dir.listFiles();
        if(list != null) {
            for (File file : list) {
                if(file.isFile()) {
                    Logger.getGlobal().info("+ File: " + file.getName());
                    directoryNode.addFileNode(file);
                } else {
                    scan(file, in);
                }
            }
        } else {
            Logger.getGlobal().warning("Failed to open directory " + dir.getPath());
        }
        return result;
    }
}
