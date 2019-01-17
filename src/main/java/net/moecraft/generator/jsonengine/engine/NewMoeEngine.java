//--------------------------------------------------
// Class BalthildEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine.engine;

import net.moecraft.generator.Environment;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class NewMoeEngine extends CommonEngine implements GeneratorEngine, ParserEngine {
    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Override
    public MetaResult decode(String data){
        MetaResult  result  = new MetaResult();
        JSONTokener tokener = new JSONTokener(data);
        JSONObject  root    = new JSONObject(tokener);
        try {
            result.setDescription(root.getString("description"))
                    .setVersion(root.getString("version"))
                    .setTime(new SimpleDateFormat(dateFormat, Locale.CHINA).parse(root.getString("update_date")).getTime());
        } catch (ParseException ex) {
            Logger.getGlobal().warning("Detected invalid datetime in json. JSON may be corrupted.");
            ex.printStackTrace();
        }
        JSONArray                syncedDirs       = root.getJSONArray("synced_dirs");
        ArrayList<DirectoryNode> syncedDirsResult = result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory);
        scanDirForDecoding(syncedDirs, syncedDirsResult);
        JSONArray syncedFiles  = root.getJSONArray("synced_files");
        JSONArray defaultFiles = root.getJSONArray("default_files");
        syncedFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.SyncedFile)));
        defaultFiles.forEach(fileObject -> addFileNodeForDecoding(fileObject, result.getFileNodesByType(MetaNodeType.DefaultFile)));
        return result;
    }

    private void addFileNodeForDecoding(Object fileObject, DirectoryNode result) {
        try {
            JSONObject object   = (JSONObject) fileObject;
            FileNode   fileNode = new FileNode(new File(basePath + "/" + object.getString("path")));
            fileNode.setExpectedMd5(object.getString("md5"))
                    .setExpectedSize(object.getLong("size"));
            result.addFileNode(fileNode);
        } catch (ClassCastException ex) {
            Logger.getGlobal().warning("Detected invalid contents in json. JSON may be corrupted.");
            ex.printStackTrace();
        }
    }

    @Override
    public String encode(MetaResult result) throws IOException {
        JSONObject object = new JSONObject();
        object.put("description", result.getDescription());
        object.put("version", result.getVersion());
        object.put("updated_time", result.getTime() / 1000);
        object.put("update_date", new SimpleDateFormat(dateFormat, Locale.CHINA).format(new Date(result.getTime())));
        JSONArray syncedDirs = new JSONArray();
        scanDirForEncoding(syncedDirs, result.getDirectoryNodesByType(MetaNodeType.SyncedDirectory));
        object.put("synced_dirs", syncedDirs);
        JSONArray syncedFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.SyncedFile).getFileNodes());
        object.put("synced_files", syncedFiles);
        JSONArray defaultFiles = addFileNodeForEncoding(result.getFileNodesByType(MetaNodeType.DefaultFile).getFileNodes());
        object.put("default_files", defaultFiles);
        return object.toString();
    }

    private JSONArray addFileNodeForEncoding(ArrayList<FileNode> fileNodes) {
        return new JSONArray() {{
            fileNodes.forEach(file -> put(prepareObjectForEncoding(file)));
        }};
    }

    private JSONObject prepareObjectForEncoding(FileNode file) {
        return new JSONObject() {{
            put("path", file.getRelativePath());
            put("size", file.getSize());
            put("md5", file.getMD5());
            JSONArray objects = new JSONArray();
            file.getObjects().forEach(objects::put);
            put("objects", objects);
        }};
    }

    @Override
    public void save(Object in) throws IOException, ClassCastException {
        String result = (String) in;
        File deployDir = new File(basePath + "/../Deployment");
        if(!deployDir.exists() && !deployDir.mkdirs())
            throw new IOException("NewMoeEngine: unable to create Deployment dir");
        writeJson(new File(basePath + "/../Deployment/updater.json"), result);
    }

    private void scanDirForEncoding(JSONArray result, ArrayList<DirectoryNode> directoryNodes) {
        for (DirectoryNode dir : directoryNodes) {
            JSONArray child = new JSONArray();
            if (dir.hasChildDirectory()) {
                scanDirForEncoding(child, dir.getDirectoryNodes());
            }
            JSONArray dirFiles = new JSONArray();
            for (FileNode file : dir.getFileNodes()) {
                try {
                    dirFiles.put(prepareObjectForEncoding(file));
                } catch (Exception ex) {
                    Logger.getGlobal().warning("Add file failed: " + file.getFile().getName());
                    ex.printStackTrace();
                }
            }
            JSONObject currentDir = new JSONObject() {{
                try {
                    put("path", getRelativePath(dir.getDirectory().getCanonicalPath()));
                    put("child", child);
                    put("files", dirFiles);
                } catch (IOException ex) {
                    Logger.getGlobal().warning("Add dir failed: " + dir.getDirectory().getName());
                    ex.printStackTrace();
                }
            }};
            result.put(currentDir);
        }
    }

    private void scanDirForDecoding(JSONArray array, ArrayList<DirectoryNode> result) {
        for (Object syncedDirObject : array) {
            JSONObject    jsonSyncedDirObject = (JSONObject) syncedDirObject;
            File          dirFile             = new File(basePath + "/" + jsonSyncedDirObject.getString("path"));
            DirectoryNode directoryNode       = new DirectoryNode(dirFile);
            JSONArray     filesArray          = jsonSyncedDirObject.getJSONArray("files");
            filesArray.forEach(fileObject -> addFileNodeForDecoding(fileObject, directoryNode));
            ArrayList<DirectoryNode> child      = directoryNode.getDirectoryNodes();
            JSONArray                childArray = jsonSyncedDirObject.getJSONArray("child");
            scanDirForDecoding(childArray, child);
            result.add(directoryNode);
        }
    }
}
