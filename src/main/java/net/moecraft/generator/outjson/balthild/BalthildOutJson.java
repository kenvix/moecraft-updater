//--------------------------------------------------
// Class BalthildOutJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.outjson.balthild;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.outjson.OutJson;
import org.json.JSONObject;
import org.json.JSONArray;

public class BalthildOutJson implements OutJson {
    public String encode(String basePath, MetaResult result) {
        JSONObject object = new JSONObject();
        object.put("updated_at", result.getTime()/1000);
        JSONArray syncedDirs = new JSONArray();
        JSONArray syncedFiles = new JSONArray();
        JSONArray defaultFiles = new JSONArray();
        for (DirectoryNode dir : result.getDirectoryNodes().get(MetaNodeType.SyncedDirectory)) {
            JSONObject currentDir = new JSONObject();
            currentDir.put("path", FileTool.getRelativePath());
            JSONArray dirFiles = new JSONArray();
            for (FileNode file : dir.getFiles()) {
                dirFiles.put(new JSONObject() {
                    {
                        put("path", );
                    }
                });
            }
        }
        /*        JSONObject syncedDirs =  object.put("synced_dirs", new Object());
        JSONObject syncedFiles = object.put("synced_files", new Object());
        JSONObject defaultFiles = object.put("default_files", new Object());*/
        return object.toString();
    }
}
