//--------------------------------------------------
// Class MetaResult
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------
package net.moecraft.generator.meta;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class MetaResult {
    private HashMap<MetaNodeType, HashSet<DirectoryNode>> directoryNodes;
    private HashMap<MetaNodeType, DirectoryNode> fileNodes;
    private String description = null;
    private long time = new Date().getTime();

    MetaResult() {
        directoryNodes = new HashMap<>();
        fileNodes = new HashMap<>();
        for(MetaNodeType type : MetaNodeType.values()) {
            if(type.getClass().isAnnotationPresent(DirectoryMetaNode.class))
                directoryNodes.put(type, new HashSet<>());
            else if(type.getClass().isAnnotationPresent(FileMetaNode.class))
                fileNodes.put(type, new DirectoryNode(new File(".")));
        }
    }

    public DirectoryNode addDirectoryNode(MetaNodeType type, DirectoryNode dir) {
        directoryNodes.get(type).add(dir);
        return dir;
    }

    public DirectoryNode addDirectoryNode(MetaNodeType type, File dir) {
        return addDirectoryNode(type, new DirectoryNode(dir));
    }

    public FileNode addFileNode(MetaNodeType type, FileNode file) {
        fileNodes.get(type).addFileNode(file);
        return file;
    }

    public FileNode addFileNode(MetaNodeType type, File file) {
        return addFileNode(type, new FileNode(file));
    }

    public String getDescription() {
        return description;
    }

    public MetaResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public HashMap<MetaNodeType, HashSet<DirectoryNode>> getDirectoryNodes() {
        return directoryNodes;
    }

    public long getTime() {
        return time;
    }
}
