//--------------------------------------------------
// Class DirectoryNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import java.io.File;
import java.util.HashSet;

public class DirectoryNode {
    private HashSet<FileNode>      fileNodes      = new HashSet<>();
    private HashSet<DirectoryNode> directoryNodes = new HashSet<>();
    private File                   directory;

    public DirectoryNode(File dir) {
        this.directory = dir;
    }

    public FileNode addFileNode(FileNode node) {
        fileNodes.add(node);
        return node;
    }

    public FileNode addFileNode(File file) {
        return addFileNode(new FileNode(file));
    }

    public DirectoryNode removeFileNode(FileNode node) {
        fileNodes.remove(node);
        return this;
    }

    public DirectoryNode addDirectoryNode(DirectoryNode dir) {
        directoryNodes.add(dir);
        return dir;
    }

    public DirectoryNode setDirectoryNodes(HashSet<DirectoryNode> directoryNodes) {
        this.directoryNodes = directoryNodes;
        return this;
    }

    public DirectoryNode addDirectoryNode(File dir) {
        return addDirectoryNode(new DirectoryNode(dir));
    }

    public File getDirectory() {
        return directory;
    }

    public HashSet<DirectoryNode> getDirectoryNodes() {
        return directoryNodes;
    }

    public HashSet<FileNode> getFileNodes() {
        return fileNodes;
    }

    public boolean hasChildDirectory() {
        return !directoryNodes.isEmpty();
    }

    public boolean hasFile() {
        return !fileNodes.isEmpty();
    }
}
