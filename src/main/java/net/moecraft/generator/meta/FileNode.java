//--------------------------------------------------
// Class FileNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;

import java.io.File;
import java.io.IOException;

public class FileNode {
    private File   file;
    private String md5          = null;

    public FileNode(File file) {
        this.file = file;
    }

    public String getMD5() {
        return md5 == null ? (md5 = FileTool.getFileMD5(file)) : md5;
    }

    public long getSize() {
        return file.length();
    }

    public File getFile() {
        return file;
    }
}
