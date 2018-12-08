//--------------------------------------------------
// Class FileTool
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileTool {

    /**
     * 获取单个文件的MD5值
     *
     * @param file file
     * @return md5
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest   digest   = null;
        FileInputStream in       = null;
        byte            buffer[] = new byte[1024];
        int             len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * Get relative path of a file
     * @param base Base path
     * @param path file path
     * @return result
     */
    public static String getRelativePath(String base, String path) {
        if(path.startsWith("./") || path.startsWith(".\\"))
            return path.substring(2);
        if(path.startsWith(base)) {
            path = path.substring(base.length());
            if(path.startsWith("/") || path.startsWith("\\"))
                path = path.substring(1);
            return path;
        }
        return path;
    }
}
