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
import java.util.logging.Logger;

public class MetaResult {
    private HashMap<MetaNodeType, HashSet<DirectoryNode>> directoryNodes;
    private HashMap<MetaNodeType, DirectoryNode> fileNodes;
    private String                               description = null;
    private String                               version = null;
    private long                                 time    = new Date().getTime();

    {
        directoryNodes = new HashMap<>();
        fileNodes = new HashMap<>();
        for (MetaNodeType type : MetaNodeType.values()) {
           try {
               if (type.getClass().getField(type.name()).isAnnotationPresent(DirectoryMetaNode.class))
                   directoryNodes.put(type, new HashSet<>());
               else if (type.getClass().getField(type.name()).isAnnotationPresent(FileMetaNode.class))
                   fileNodes.put(type, new DirectoryNode(new File(".")));
               else {
                   directoryNodes.put(type, new HashSet<>());
                   fileNodes.put(type, new DirectoryNode(new File(".")));
               }
           } catch (Exception ex) {
               Logger.getGlobal().warning("Invalid field during MetaResult:MetaNodeType initialize. may cause NullPointerException.");
               ex.printStackTrace();
           }
        }
    }

    public DirectoryNode addDirectoryNode(MetaNodeType type, DirectoryNode dir) {
        if(!directoryNodes.containsKey(type))
            directoryNodes.put(type, new HashSet<>());
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

    public String getVersion() {
        return version;
    }

    public MetaResult setVersion(String version) {
        this.version = version;
        return this;
    }


    public MetaResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public HashMap<MetaNodeType, HashSet<DirectoryNode>> getDirectoryNodes() {
        return directoryNodes;
    }

    public HashSet<DirectoryNode> getDirectoryNodesByType(MetaNodeType type) {
        return directoryNodes.get(type);
    }

    public HashMap<MetaNodeType, DirectoryNode> getFileNodes() {
        return fileNodes;
    }

    public DirectoryNode getFileNodesByType(MetaNodeType type) {
        return fileNodes.get(type);
    }

    public long getTime() {
        return time;
    }
}
