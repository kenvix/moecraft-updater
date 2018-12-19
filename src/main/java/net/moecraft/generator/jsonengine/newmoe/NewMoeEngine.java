//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.newmoe;
import com.kenvix.utils.FileTool;
import net.moecraft.generator.jsonengine.CommonEngine;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

public abstract class NewMoeEngine extends CommonEngine implements GeneratorEngine, ParserEngine {

    public String encode(String basePath, MetaResult result) throws IOException {
        this.basePath = basePath;
        JSONObject object = new JSONObject();
        object.put("description", result.getDescription());
        object.put("version", result.getVersion());
        object.put("updated_time", result.getTime() / 1000);
        object.put("update_date", (new Date(result.getTime())).toString());
        JSONArray syncedDirs = new JSONArray();
        scanDir(syncedDirs, result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        object.put("synced_dirs", syncedDirs);
        JSONArray syncedFiles = new JSONArray() {{
            for (FileNode file : result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes()) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("size", file.getSize());
                    put("md5", file.getMD5());
                }});
            }
        }};
        JSONArray defaultFiles = new JSONArray() {{
            for (FileNode file : result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes()) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("size", file.getSize());
                    put("md5", file.getMD5());
                }});
            }
        }};
        return object.toString();
    }

    public void save(String basePath, Object in) throws IOException, ClassCastException {
        String result = (String) in;
        writeJson(new File(basePath + "/updater.json"), result);
    }

    private void scanDir(JSONArray result, HashSet<DirectoryNode> directoryNodes) throws IOException {
        for (DirectoryNode dir : directoryNodes) {
            JSONArray child = new JSONArray();
            if (dir.hasChildDirectory()) {
                scanDir(child, dir.getDirectoryNodes());
            }
            JSONArray dirFiles = new JSONArray();
            for (FileNode file : dir.getFileNodes()) {
                dirFiles.put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("size", file.getSize());
                    put("md5", file.getMD5());
                }});
            }
            JSONObject currentDir = new JSONObject() {{
                put("path", getRelativePath(dir.getDirectory().getCanonicalPath()));
                put("child", child);
                put("files", dirFiles);
            }};
            result.put(currentDir);
        }
    }
}
