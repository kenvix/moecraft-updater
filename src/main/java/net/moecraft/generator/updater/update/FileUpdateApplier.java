//--------------------------------------------------
// Class FileUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update;

import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaResult;

public final class FileUpdateApplier {
    private final MetaResult task;

    public FileUpdateApplier(MetaResult task) {
        this.task = task;
    }

    public void start() {

    }

    private void handleDirectory(DirectoryNode directoryNode) {

    }

    private void handleFile(FileNode fileNode) {

    }

    private class FileHandler {

        private boolean delete(FileNode fileNode) {
            return fileNode.getFile().delete();
        }

        private void copy(FileNode fileNode) {

        }

        private void link(FileNode fileNode) {

        }
    }

    private class DirectoryHandler {

        private boolean delete(DirectoryNode directoryNode) {
            return directoryNode.getDirectory().delete();
        }

        private boolean create(DirectoryNode directoryNode) {
            return directoryNode.getDirectory().mkdirs();
        }
    }
}
