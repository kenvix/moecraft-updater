//--------------------------------------------------
// Class MetaResult
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------
package net.moecraft.generator.meta;

import net.moecraft.generator.Environment;
import net.moecraft.generator.updater.update.selfupdate.UpdaterInfo;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class MetaResult implements Cloneable {
    private Map<MetaNodeType, List<DirectoryNode>>          directoryNodes;
    private Map<MetaNodeType, DirectoryNode>                fileNodes;
    private Map<String, List<FileNode>>                     globalObjects = new HashMap<>();
    private String                                          description   = null;
    private String                                          version       = null;
    private long                                            objectSize    = 0;
    private long                                            time          = new Date().getTime();
    private UpdaterInfo                                     updaterInfo   = null;

    {
        directoryNodes = new HashMap<>();
        fileNodes = new HashMap<>();
        for (MetaNodeType type : MetaNodeType.values()) {
            try {
                if (type.getClass().getField(type.name()).isAnnotationPresent(DirectoryMetaNode.class))
                    directoryNodes.put(type, new ArrayList<>());
                else if (type.getClass().getField(type.name()).isAnnotationPresent(FileMetaNode.class))
                    fileNodes.put(type, new DirectoryNode(new File(".")));
                else {
                    directoryNodes.put(type, new ArrayList<>());
                    fileNodes.put(type, new DirectoryNode(new File(".")));
                }
            } catch (Exception ex) {
                Environment.getLogger().warning("Invalid field during MetaResult:MetaNodeType initialize. may cause NullPointerException.");
                ex.printStackTrace();
            }
        }
    }

    public List<FileNode> getGlobalObjectsByMd5(String md5) {
        return globalObjects.get(md5);
    }

    public Map<String, List<FileNode>> getGlobalObjects() {
        return globalObjects;
    }

    public MetaResult putGlobalObjectsByMd5(String md5, List<FileNode> data) {
        globalObjects.put(md5, data);
        return this;
    }

    public boolean hasGlobalObject(String md5) {
        return globalObjects.containsKey(md5);
    }

    public DirectoryNode addDirectoryNode(MetaNodeType type, DirectoryNode dir) {
        if (!directoryNodes.containsKey(type))
            directoryNodes.put(type, new ArrayList<>());
        directoryNodes.get(type).add(dir);
        return dir;
    }

    public UpdaterInfo getUpdaterInfo() {
        return updaterInfo;
    }

    public MetaResult setUpdaterInfo(UpdaterInfo updaterInfo) {
        this.updaterInfo = updaterInfo;
        return this;
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

    public Map<MetaNodeType, List<DirectoryNode>> getDirectoryNodes() {
        return directoryNodes;
    }

    public List<DirectoryNode> getDirectoryNodesByType(MetaNodeType type) {
        return directoryNodes.get(type);
    }

    @Deprecated
    public MetaResult setDirectoryNodesByType(MetaNodeType type, ArrayList<DirectoryNode> data) {
        directoryNodes.replace(type, data);
        return this;
    }

    public Map<MetaNodeType, DirectoryNode> getFileNodes() {
        return fileNodes;
    }

    public DirectoryNode getFileNodesByType(MetaNodeType type) {
        return fileNodes.get(type);
    }

    public long getTime() {
        return time;
    }

    public MetaResult setTime(long time) {
        this.time = time;
        return this;
    }

    public long getObjectSize() {
        return objectSize;
    }

    public MetaResult setObjectSize(long objectSize) {
        this.objectSize = objectSize;
        return this;
    }

    @Nullable
    @Override
    protected MetaResult clone() {
        MetaResult metaResult = null;

        try {
            metaResult = (MetaResult) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

        if(metaResult != null) {
            HashMap<MetaNodeType, List<DirectoryNode>> newDirectoryNodes = new HashMap<>();
            this.directoryNodes.forEach((nodeType, nodeList) ->
                newDirectoryNodes.put(nodeType, new ArrayList<DirectoryNode>() {{
                    directoryNodes.get(nodeType).forEach(node -> this.add(node.clone()));
                }})
            );
            metaResult.directoryNodes = newDirectoryNodes;

            HashMap<MetaNodeType, DirectoryNode>            newFileNodes = new HashMap<>();
            this.fileNodes.forEach((nodeType, node) -> newFileNodes.put(nodeType, node.clone()));
            metaResult.fileNodes = newFileNodes;
        }

        return metaResult;
    }
}
