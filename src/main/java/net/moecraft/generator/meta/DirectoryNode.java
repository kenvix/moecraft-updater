//--------------------------------------------------
// Class DirectoryNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNode implements Cloneable {
    private List<FileNode> fileNodes = new ArrayList<>();
    private List<DirectoryNode> directoryNodes = new ArrayList<>();
    private File directory;
    private Path path;

    public DirectoryNode(File dir) {
        this.directory = dir;
        this.path = dir.toPath();
    }

    public DirectoryNode(Path dir) {
        this.directory = dir.toFile();
        this.path = dir;
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

    public Path getPath() {
        return path;
    }

    @Deprecated
    public DirectoryNode setDirectoryNodes(ArrayList<DirectoryNode> directoryNodes) {
        this.directoryNodes = directoryNodes;
        return this;
    }

    public DirectoryNode addDirectoryNode(File dir) {
        return addDirectoryNode(new DirectoryNode(dir));
    }

    public File getDirectory() {
        return directory;
    }

    public List<DirectoryNode> getDirectoryNodes() {
        return directoryNodes;
    }

    public List<FileNode> getFileNodes() {
        return fileNodes;
    }

    /**
     * 找到所有的子目录的文件输出
     * @return
     */
    public List<FileNode> allContainFileNodes() {
        List<FileNode> back = (List<FileNode>) ((ArrayList)fileNodes).clone();
        directoryNodes.forEach(dn->{dfs(dn, back);});
        return back;
    }

    private List<FileNode> dfs(DirectoryNode root, List<FileNode> back){
        back.addAll(root.fileNodes);
        directoryNodes.forEach(dn->{dfs(dn, back);});
        return back;
    }

    public boolean hasChildDirectory() {
        return !directoryNodes.isEmpty();
    }

    public boolean hasFile() {
        return !fileNodes.isEmpty();
    }

    @Override
    public String toString() {
        return directory.getName();
    }

    @Override
    public int hashCode() {
        return 0xFA01 + path.hashCode() ^ fileNodes.hashCode() ^ directoryNodes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DirectoryNode) {
            return ((DirectoryNode) obj).getPath().equals(path);
        }
        return false;
    }

    /**
     * Clone DirectoryNode. Including all child DirectoryNode and child FileNode.
     * Note: java.io.File object will NOT be cloned for its meaningless.
     *
     * @return DirectoryNode
     */
    @Nullable
    @Override
    protected DirectoryNode clone() {
        DirectoryNode directoryNode = null;

        try {
            directoryNode = (DirectoryNode) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

        if (directoryNode != null) {
            ArrayList<FileNode> newFileNodes = new ArrayList<>();
            this.fileNodes.forEach(node -> newFileNodes.add(node.clone()));
            directoryNode.fileNodes = newFileNodes;

            ArrayList<DirectoryNode> newDirectoryNodes = new ArrayList<>();
            this.directoryNodes.forEach(node -> newDirectoryNodes.add(node.clone()));
            directoryNode.directoryNodes = newDirectoryNodes;
        }

        return directoryNode;
    }
}
