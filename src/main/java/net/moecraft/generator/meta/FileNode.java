//--------------------------------------------------
// Class FileNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;

import java.io.File;

public class FileNode {
    private File   file;
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
     * Expected file size. Auto filled by ParserEngine
     */
    private long expectedSize = -1;

    public FileNode(File file) {
        this.file = file;
    }

    public final String getMD5() {
        return md5 == null ? (md5 = FileTool.getFileMD5(file)) : md5;
    }

    public final long getSize() {
        return size == -1 ? (size = file.length()) : size;
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

    public final long getExpectedSize() {
        return expectedSize;
    }

    public final FileNode setExpectedSize(long exceptedSize) {
        this.expectedSize = exceptedSize;
        return this;
    }
}
