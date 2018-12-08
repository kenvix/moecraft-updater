//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.balthild;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;

public class BalthildEngine implements GeneratorEngine {
    private String basePath;

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

    public void save(String basePath, Object in) throws IOException, ClassCastException {
        String result = (String) in;
        File target = new File(basePath + "/metadata.json");
        if(target.exists())
            if(!target.delete()) {
                Logger.getGlobal().warning("Unable to delete: " + target.getName() + " . Generation failed");
                return;
            }
        FileWriter writer = new FileWriter(target);
        writer.write(result);
        writer.close();
    }

    private void scanDir(JSONArray result, HashSet<DirectoryNode> directoryNodes) throws IOException {
        for (DirectoryNode dir : directoryNodes) {
            if (dir.hasChildDirectory()) {
                scanDir(result, dir.getDirectoryNodes());
            }
            dir.getDirectoryNodes().forEach(dirx -> System.out.println(dirx.getDirectory().getPath()));
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

    private String getRelativePath(String path) {
        return FileTool.getRelativePath(basePath, path).replace('\\', '/');
    }
}
