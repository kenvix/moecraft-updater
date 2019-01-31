//--------------------------------------------------
// Class DirectoryHandler
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.meta.DirectoryNode;

public class DirectoryHandler {

    private boolean delete(DirectoryNode directoryNode) {
        return directoryNode.getDirectory().delete();
    }

    private boolean create(DirectoryNode directoryNode) {
        return directoryNode.getDirectory().mkdirs();
    }
}