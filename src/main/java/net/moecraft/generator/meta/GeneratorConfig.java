//--------------------------------------------------
// Class GeneratorConfig
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratorConfig extends MetaResult {
    private File file;
    private String basePath;
    private HashSet<String> excludedFileRule = new HashSet<>();
    private HashSet<String> excludedDirectoryRule = new HashSet<>();
    private static GeneratorConfig instance = null;

    public GeneratorConfig getInstance(String basePath, File file) throws IOException {
        return instance == null ? (instance = new GeneratorConfig(basePath, file)) : instance;
    }

    public GeneratorConfig getInstance() throws IllegalStateException {
        if(instance == null)
            throw new IllegalStateException("GeneratorConfig not initialized!!!");
        return instance;
    }

    private GeneratorConfig(String basePath, File file) throws IOException {
        this.file = file;
        basePath = basePath.replace('\\','/');
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";
        scan();
    }

    private void scan() throws IOException {
        JSONParser parser = new JSONParser(FileTool.readAllText(file.getAbsolutePath()), Global.instance(), false);
        JSONObject json = (JSONObject) parser.parse();
        setDescription(json.getString("description"));
        setVersion(json.getString("version"));
        searchFileItems("synced_dirs", MetaNodeType.SyncedDirectory, json);
        searchFileItems("synced_files", MetaNodeType.SyncedFile, json);
        searchFileItems("default_files", MetaNodeType.DefaultFile, json);
        searchRuleItems("excluded_files", excludedFileRule, json);
        searchRuleItems("excluded_dir", excludedDirectoryRule, json);
    }

    private void searchRuleItems(String key, HashSet<String> target, JSONObject json) {
        for (Object item : json.getJSONArray("excluded_files")) {
            if(item instanceof String) {
                target.add(((String) item).replace('\\', '/'));
            } else {
                Logger.getGlobal().info("Detected invalid config item " + key + " on " + target.getClass().getName());
            }
        }
    }

    private void searchFileItems(String key, MetaNodeType type, JSONObject json) {
        try {
            boolean isDirectory = type.getClass().getField(type.name()).isAnnotationPresent(DirectoryMetaNode.class);
            boolean isFile = type.getClass().getField(type.name()).isAnnotationPresent(FileMetaNode.class);
            for (Object dir : json.getJSONArray(key)) {
                if(dir instanceof String) {
                    File dirFile = new File(basePath + dir);
                    if(dirFile.exists()) {
                        if(isDirectory && !dirFile.isFile()) {
                            DirectoryNode directoryNode = new DirectoryNode(dirFile);
                            addDirectoryNode(type, directoryNode);
                        } else if(isFile && dirFile.isFile()) {
                            FileNode fileNode = new FileNode(dirFile);
                            addFileNode(type, fileNode);
                        }
                    } else {
                        Logger.getGlobal().log(Level.INFO, "Declaring a not-found or invalid file/directory" + dir + ". Skip...");
                    }
                } else {
                    Logger.getGlobal().info("Detected invalid config item " + key + " on " + type.name());
                }
            }
        } catch (NoSuchFieldException ex) {
            Logger.getGlobal().log(Level.CONFIG, "Declaring a invalid field [" + ex.getMessage() + "] Skip...");
        }
    }

    public File getFile() {
        return file;
    }


}
