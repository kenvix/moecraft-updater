//--------------------------------------------------
// Class FileNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FileNode implements Cloneable {
    private File   file;
    private Path path;

    /**
     * Actual file md5. Auto filled by getMD5(). Will NOT be autofilled by ParserEngine
     */
    private String md5 = null;

    /**
     * Expected md5. Auto filled by ParserEngine
     */
    private String expectedMd5 = null;

    /**
     * Actual file md5. Auto filled by getMD5(). Will NOT be autofilled by ParserEngine
     */
    private long size = -1;

    /**
     * Actual file time. Auto filled by getTime(). Will NOT be autofilled by ParserEngine
     */
    private long time = -1;

    /**
     * Expected file size. Auto filled by ParserEngine
     */
    private long expectedSize = -1;

    /**
     * relative Path
     */
    private String relativePath = null;

    /**
     * Blocked Objects
     */
    private List<FileNode> objects = null;

    public FileNode(File file) {
        this.path = file.toPath();
        this.file = file;
    }

    public FileNode(Path path) {
        this.path = path;
        this.file = path.toFile();
    }

    public final String getMD5() {
        return md5 == null ? (md5 = FileTool.getFileMD5(file)) : md5;
    }

    public final long getSize() {
        return size == -1 ? (size = file.length()) : size;
    }

    public final long getTime() {
        return time == -1 ? (time = file.lastModified()) : time;
    }

    public final File getFile() {
        return file;
    }

    public final String getExpectedMd5() {
        return expectedMd5;
    }

    public final FileNode setExpectedMd5(String expectedMd5) {
        this.expectedMd5 = expectedMd5;
        return this;
    }

    public final Path getPath() {
        return path;
    }

    public String getRelativePath() {
        if(relativePath == null) {
            if(file.exists()) {
                try {
                    relativePath = FileTool.getRelativePath(Environment.getBaseMoeCraftPath(), file.getCanonicalPath());
                } catch (IOException ex) {
                    relativePath = null;
                }
            }
            if(relativePath == null)
                relativePath = FileTool.getRelativePath(Environment.getBaseMoeCraftPath(), file.getPath());
        }
        return relativePath;
    }


    public final long getExpectedSize() {
        return expectedSize;
    }

    public final FileNode setExpectedSize(long exceptedSize) {
        this.expectedSize = exceptedSize;
        return this;
    }

    public final List<FileNode> getObjects() {
        if(objects == null) {
            synchronized (FileNode.class) {
                if(objects == null)
                    objects = new ArrayList<>();
            }
        }
        return objects;
    }

    public final FileNode setObjects(ArrayList<FileNode> objects) {
        this.objects = objects;
        return this;
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public int hashCode() {
        return getRelativePath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileNode) {
            return obj.hashCode() == hashCode();
        }
        return false;
    }

    /**
     * Clone FileNode. Including all child DirectoryNode and child FileNode.
     * Note: java.io.File object will NOT be cloned for its meaningless.
     * @return FileNode
     */
    @Override
    protected FileNode clone() {
        FileNode fileNode = null;
        try {
            fileNode = (FileNode) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        if(fileNode != null) {
            ArrayList<FileNode> newFileNodes  = new ArrayList<>();
            this.objects.forEach(node -> newFileNodes.add(node.clone()));
            fileNode.objects = newFileNodes;
        }
        return fileNode;
    }
}
