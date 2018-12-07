//--------------------------------------------------
// Class DirectoryNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import java.io.File;
import java.util.HashSet;

public class DirectoryNode {
    private HashSet<FileNode> files = new HashSet<>();
    private File   dir;

    public DirectoryNode(File dir) {
        this.dir = dir;
    }

    public HashSet<FileNode> getFiles() {
        return files;
    }

    public FileNode addFileNode(FileNode node) {
        files.add(node);
        return node;
    }

    public FileNode addFileNode(File file) {
        return addFileNode(new FileNode(file));
    }

    public DirectoryNode removeFileNode(FileNode node) {
        files.remove(node);
        return this;
    }

    public File getDirectory() {
        return dir;
    }
}
