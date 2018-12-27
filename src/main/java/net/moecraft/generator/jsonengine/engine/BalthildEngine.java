//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.engine;

import net.moecraft.generator.jsonengine.CommonEngine;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * Balthild Engine
 * only used for compate balthild updater.
 */
public class BalthildEngine extends CommonEngine implements GeneratorEngine {

    public String encode(String basePath, MetaResult result) throws IOException {
        this.basePath = basePath;
        JSONObject object = new JSONObject();
        object.put("updated_at", result.getTime() / 1000);
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
        object.put("synced_files", syncedFiles);
        JSONArray defaultFiles = new JSONArray() {{
            for (FileNode file : result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes()) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("md5", file.getMD5());
                }});
            }
        }};
        object.put("default_files", defaultFiles);
        return object.toString();
    }

    public void save(String basePath, Object in) throws IOException, ClassCastException {
        String result = (String) in;
        writeJson(new File(basePath + "/metadata.json"), result);
    }

    private void scanDir(JSONArray result, HashSet<DirectoryNode> directoryNodes) throws IOException {
        for (DirectoryNode dir : directoryNodes) {
            if (dir.hasChildDirectory()) {
                scanDir(result, dir.getDirectoryNodes());
            }
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
