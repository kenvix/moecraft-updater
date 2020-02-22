//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.engine;

import net.moecraft.generator.Environment;
import net.moecraft.generator.jsonengine.GeneratorEngine;
import net.moecraft.generator.jsonengine.ParserEngine;
import net.moecraft.generator.meta.*;
import net.moecraft.generator.updater.update.selfupdate.UpdaterInfo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NewMoeEngine extends CommonEngine implements GeneratorEngine, ParserEngine {
    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Override
    public MetaResult decode(String data) {
        MetaResult result = new MetaResult();

        JSONTokener tokener = new JSONTokener(data);
        JSONObject root = new JSONObject(tokener);

        try {
            String moecraftVersion = root.getString("version");

            result.setDescription(root.getString("description"))
                    .setVersion(moecraftVersion)
                    .setTime(new SimpleDateFormat(dateFormat, Locale.CHINA).parse(root.getString("update_date")).getTime());
        } catch (ParseException ex) {
            Environment.getLogger().warning("Detected invalid datetime in json. JSON may be corrupted.");
            ex.printStackTrace();
        }

        JSONObject objects = root.getJSONObject("objects");
        objects.keySet().forEach(key -> {
            if (!result.hasGlobalObject(key)) {
                List<FileNode> objectList = new ArrayList<>();

                JSONArray currentObject = objects.getJSONArray(key);
                currentObject.forEach(jsonObject -> {
                    JSONObject json = (JSONObject) jsonObject;

                    FileNode objectNode = new FileNode(new File(json.getString("path")));
                    objectNode.setExpectedMd5(json.getString("md5"));
                    objectNode.setExpectedSize(json.getLong("size"));

                    objectList.add(objectNode);
                });

                result.putGlobalObjectsByMd5(key, objectList);
            }
        });

        JSONArray syncedDirs = root.getJSONArray("synced_dirs");
        List<DirectoryNode> syncedDirsResult = result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory);
        scanDirForDecoding(syncedDirs, syncedDirsResult, result.getGlobalObjects());

        if (root.has("default_dirs")) {
            JSONArray defaultDirs = root.getJSONArray("default_dirs");
            List<DirectoryNode> defaultDirsResult = result.getDirectoryNodesByType(MetaNodeType.DefaultDirectory);
            scanDirForDecoding(defaultDirs, defaultDirsResult, result.getGlobalObjects());
        }

        JSONArray syncedFiles = root.getJSONArray("synced_files");
        syncedFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.SyncedFile), result.getGlobalObjects()));

        JSONArray defaultFiles = root.getJSONArray("default_files");
        defaultFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.DefaultFile), result.getGlobalObjects()));

        if (root.has("updater_info") && root.getJSONObject("updater_info") != null) {
            JSONObject updaterInfoJsonObject = root.getJSONObject("updater_info");

            UpdaterInfo updaterInfo = new UpdaterInfo(updaterInfoJsonObject.getInt("version_code"));
            updaterInfo.setVersionName(updaterInfoJsonObject.getString("version_name"));
            updaterInfo.setObjectFile(getFileNodeForDecoding(root.getJSONObject("object"), result.getGlobalObjects()));

            result.setUpdaterInfo(updaterInfo);
        }

        JSONObject generatorConfigObject = root.getJSONObject("generator_config");
        GeneratorConfig.initialize(generatorConfigObject).startScan();
        return result;
    }

    private void addFileNodeForDecoding(Object jsonObject, DirectoryNode result, Map<String, List<FileNode>> globalObjects) {
        try {
            FileNode fileNode = getFileNodeForDecoding((JSONObject) jsonObject, globalObjects);

            result.addFileNode(fileNode);
        } catch (ClassCastException ex) {
            Environment.getLogger().warning("Detected invalid contents in json. JSON may be corrupted.");
            ex.printStackTrace();
        }
    }

    @NotNull
    private FileNode getFileNodeForDecoding(JSONObject jsonObject, Map<String, List<FileNode>> globalObjects) {
        FileNode fileNode = new FileNode(new File(basePath + "/" + jsonObject.getString("path")));
        String fileMd5 = jsonObject.getString("md5");

        fileNode.setExpectedMd5(fileMd5)
                .setExpectedSize(jsonObject.getLong("size"))
                .setObjects(globalObjects.get(fileMd5));
        return fileNode;
    }

    @Override
    public String encode(MetaResult result) throws IOException {
        JSONObject object = new JSONObject();

        object.put("description", result.getDescription());
        object.put("version", result.getVersion());
        object.put("updated_time", result.getTime() / 1000);
        object.put("update_date", new SimpleDateFormat(dateFormat, Locale.CHINA).format(new Date(result.getTime())));

        if (result.getUpdaterInfo() != null && result.getUpdaterInfo().getObjectFile() != null) {
            JSONObject updaterInfoJsonObject = new JSONObject();
            updaterInfoJsonObject.put("version_code", result.getUpdaterInfo().getVersionCode());
            updaterInfoJsonObject.put("version_name", result.getUpdaterInfo().getVersionName());
            updaterInfoJsonObject.put("object", getJsonObjectForEncoding(result.getUpdaterInfo().getObjectFile()));

            object.put("updater_info", updaterInfoJsonObject);
        }

        JSONArray syncedDirs = new JSONArray();
        scanDirForEncoding(syncedDirs, result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        object.put("synced_dirs", syncedDirs);

        JSONArray defaultDirs = new JSONArray();
        scanDirForEncoding(defaultDirs, result.getDirectoryNodesByType(MetaNodeType.DefaultDirectory));
        object.put("default_dirs", defaultDirs);

        JSONArray syncedFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes());
        object.put("synced_files", syncedFiles);

        JSONArray defaultFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes());
        object.put("default_files", defaultFiles);

        JSONObject objects = new JSONObject();
        result.getGlobalObjects().forEach((key, value) -> {
            JSONArray objectFiles = addFileNodeForEncoding(value);
            objects.put(key, objectFiles);
        });
        object.put("objects", objects);

        object.put("generator_config", GeneratorConfig.getInstance().getJsonObject());
        return object.toString();
    }

    private JSONArray addFileNodeForEncoding(List<FileNode> fileNodes) {
        return new JSONArray() {{
            fileNodes.forEach(file -> put(getJsonObjectForEncoding(file)));
        }};
    }

    private JSONObject getJsonObjectForEncoding(FileNode file) {
        return new JSONObject() {{
            put("size", file.getSize());
            put("md5", file.getMD5());

            if (!file.isObject()) {
                put("path", file.getRelativePath());

                //JSONArray objects = new JSONArray();
                //file.getObjects().forEach(objects::put);
                //put("objects", objects);
            } else {
                put("path", file.getFile().getName());
            }
        }};
    }

    @Override
    public void save(Object in) throws IOException, ClassCastException {
        String result = (String) in;
        File deployDir = Environment.getDeployPath().toFile();
        if (!deployDir.exists() && !deployDir.mkdirs())
            throw new IOException("NewMoeEngine: unable to create Deployment dir");
        writeJson(Environment.getDeployPath().resolve(Environment.getOutJsonName()).toFile(), result);
    }

    private void scanDirForEncoding(JSONArray result, List<DirectoryNode> directoryNodes) {
        for (DirectoryNode dir : directoryNodes) {
            JSONArray child = new JSONArray();
            if (dir.hasChildDirectory()) {
                scanDirForEncoding(child, dir.getDirectoryNodes());
            }
            JSONArray dirFiles = new JSONArray();
            for (FileNode file : dir.getFileNodes()) {
                try {
                    dirFiles.put(getJsonObjectForEncoding(file));
                } catch (Exception ex) {
                    Environment.getLogger().warning("Add file failed: " + file.getFile().getName());
                    ex.printStackTrace();
                }
            }
            JSONObject currentDir = new JSONObject() {{
                try {
                    put("path", getRelativePath(dir.getDirectory().getCanonicalPath()));
                    put("child", child);
                    put("files", dirFiles);
                } catch (IOException ex) {
                    Environment.getLogger().warning("Add dir failed: " + dir.getDirectory().getName());
                    ex.printStackTrace();
                }
            }};
            result.put(currentDir);
        }
    }

    private void scanDirForDecoding(JSONArray array, List<DirectoryNode> scanResult, Map<String, List<FileNode>> globalObjects) {
        for (Object syncedDirObject : array) {
            JSONObject jsonSyncedDirObject = (JSONObject) syncedDirObject;
            File dirFile = new File(basePath + "/" + jsonSyncedDirObject.getString("path"));

            DirectoryNode directoryNode = new DirectoryNode(dirFile);
            JSONArray filesArray = jsonSyncedDirObject.getJSONArray("files");

            filesArray.forEach(fileObject -> addFileNodeForDecoding(fileObject, directoryNode, globalObjects));

            List<DirectoryNode> child = directoryNode.getDirectoryNodes();
            JSONArray childArray = jsonSyncedDirObject.getJSONArray("child");
            scanDirForDecoding(childArray, child, globalObjects);

            scanResult.add(directoryNode);
        }
    }
}
