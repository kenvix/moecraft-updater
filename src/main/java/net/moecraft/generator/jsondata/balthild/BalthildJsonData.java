//--------------------------------------------------
// Class BalthildJsonData
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsondata.balthild;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.jsondata.InJsonData;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.jsondata.OutJsonData;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class BalthildJsonData implements OutJsonData {
    private String basePath;

    public String encode(String basePath, MetaResult result) throws IOException {
        this.basePath = basePath;
        JSONObject object = new JSONObject();
        object.put("updated_at", result.getTime()/1000);
        JSONArray syncedDirs = new JSONArray();
        scanDir(syncedDirs, result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        object.put("synced_dirs", syncedDirs);
        JSONArray syncedFiles = new JSONArray() {{
            for (FileNode file : result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes()) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("md5", file.getMD5());
                }});
            }
        }};
        JSONArray defaultFiles = new JSONArray() {{
            for (FileNode file : result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes()) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("md5", file.getMD5());
                }});
            }
        }};
        return object.toString();
    }

    private void scanDir(JSONArray result, HashSet<DirectoryNode> directoryNodes) throws IOException {
        for (DirectoryNode dir : directoryNodes) {
            if(dir.hasChildDirectory()) {
                scanDir(result, dir.getDirectoryNodes());
            } else {
                JSONArray dirFiles = new JSONArray();
                for (FileNode file : dir.getFileNodes()) {
                    dirFiles.put(new JSONObject() {{
                        put("path", getRelativePath(file.getFile().getCanonicalPath()));
                        put("md5", file.getMD5());
                    }});
                }
                JSONObject currentDir = new JSONObject() {{
                    put("path", getRelativePath(dir.getDirectory().getCanonicalPath()));
                    put("files", dirFiles);
                }};
                result.put(currentDir);
            }
        }
    }

    private String getRelativePath(String path) {
        return FileTool.getRelativePath(basePath, path).replace('\\', '/');
    }
}
