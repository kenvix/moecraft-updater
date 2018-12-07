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
    private HashMap<MetaNodeType, HashSet<DirectoryNode>> nodes = new HashMap<MetaNodeType, HashSet<DirectoryNode>>() {
        {
            for(MetaNodeType type : MetaNodeType.values())
                put(type, new HashSet<>());
        }
    };
    private String description = null;
    private long time = new Date().getTime();

    public DirectoryNode addDirectoryNode(MetaNodeType type, DirectoryNode dir) {
        nodes.get(type).add(dir);
        return dir;
    }

    public DirectoryNode addDirectoryNode(MetaNodeType type, File dir) {
        return addDirectoryNode(type, new DirectoryNode(dir));
    }

    public String getDescription() {
        return description;
    }

    public MetaResult setDescription(String description) {
        this.description = description;
        return this;
    }

    public HashMap<MetaNodeType, HashSet<DirectoryNode>> getNodes() {
        return nodes;
    }
}
