//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.engine;
import net.moecraft.generator.jsonengine.CommonEngine;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import org.json.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

public class NewMoeEngine extends CommonEngine implements GeneratorEngine, ParserEngine {

    public MetaResult decode(String basePath, String data) {
        MetaResult result = new MetaResult();
        JSONTokener tokener = new JSONTokener(data);
        JSONObject root = new JSONObject(tokener);
        result.setDescription(root.getString("description"))
                .setVersion(root.getString("version"))
                .setTime(root.getLong("updated_time"));
        JSONArray syncedDirs = root.getJSONArray("synced_dirs");
        HashSet<DirectoryNode> syncedDirsResult = new HashSet<>();
        scanDirForDecoding(syncedDirs, syncedDirsResult);
        result.setDirectoryNodesByType(MetaNodeType.SyncedDirectory, syncedDirsResult);
        JSONArray syncedFiles = root.getJSONArray("synced_files");
        JSONArray defaultFiles = root.getJSONArray("default_files");
        syncedFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.SyncedFile)));
        defaultFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.DefaultFile)));
        return result;
    }

    private void addFileNodeForDecoding(Object fileObject, DirectoryNode result) {
        JSONObject object = (JSONObject) fileObject;
        FileNode fileNode = new FileNode(new File(object.getString("path")));
        fileNode.setExpectedMd5(object.getString("md5"))
                .setExpectedSize(object.getLong("size"));
        result.addFileNode(fileNode);
    }

    public String encode(String basePath, MetaResult result) throws IOException {
        this.basePath = basePath;
        JSONObject object = new JSONObject();
        object.put("description", result.getDescription());
        object.put("version", result.getVersion());
        object.put("updated_time", result.getTime() / 1000);
        object.put("update_date", (new Date(result.getTime())).toString());
        JSONArray syncedDirs = new JSONArray();
        scanDirForEncoding(syncedDirs, result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        object.put("synced_dirs", syncedDirs);
        JSONArray syncedFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes());
        object.put("synced_files", syncedFiles);
        JSONArray defaultFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes());
        object.put("default_files", defaultFiles);
        return object.toString();
    }

    private JSONArray addFileNodeForEncoding(HashSet<FileNode> fileNodes) throws IOException {
        return new JSONArray() {{
            for (FileNode file : fileNodes) {
                put(new JSONObject() {{
                    put("path", getRelativePath(file.getFile().getCanonicalPath()));
                    put("size", file.getSize());
                    put("md5", file.getMD5());
                }});
            }
        }};
    }

    public void save(String basePath, Object in) throws IOException, ClassCastException {
        String result = (String) in;
        writeJson(new File(basePath + "/updater.json"), result);
    }

    private void scanDirForEncoding(JSONArray result, HashSet<DirectoryNode> directoryNodes) throws IOException {
        for (DirectoryNode dir : directoryNodes) {
            JSONArray child = new JSONArray();
            if (dir.hasChildDirectory()) {
                scanDirForEncoding(child, dir.getDirectoryNodes());
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

    private void scanDirForDecoding(JSONArray array, HashSet<DirectoryNode> result) {
        for (Object syncedDirObject: array) {
            JSONObject jsonSyncedDirObject = (JSONObject) syncedDirObject;
            File dirFile = new File(jsonSyncedDirObject.getString("path"));
            DirectoryNode directoryNode = new DirectoryNode(dirFile);
            JSONArray filesArray = jsonSyncedDirObject.getJSONArray("files");
            filesArray.forEach(fileObject -> addFileNodeForDecoding(fileObject, directoryNode));
            HashSet<DirectoryNode> child = new HashSet<>();
            JSONArray childsArray = jsonSyncedDirObject.getJSONArray("child");
            scanDirForDecoding(childsArray, child);
            result.add(directoryNode);
        }
    }
}
