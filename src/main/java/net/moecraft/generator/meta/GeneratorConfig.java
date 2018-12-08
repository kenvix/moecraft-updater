//--------------------------------------------------
// Class GeneratorConfig
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;
import com.kenvix.utils.StringTool;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratorConfig extends MetaResult {
    private File file;
    private String basePath;
    private        HashSet<String> excludedFileRule      = new HashSet<>();
    private        HashSet<String> excludedDirectoryRule = new HashSet<>();
    private static GeneratorConfig instance              = null;

    /**
     * Get an instance of GeneratorConfig
     * @param basePath base path of application
     * @param file generator_config.json
     * @return GeneratorConfig
     * @throws IOException failed to read file
     */
    public static GeneratorConfig getInstance(String basePath, File file) throws IOException {
        return instance == null ? (instance = new GeneratorConfig(basePath, file)) : instance;
    }

    /**
     * Directly get an instance of GeneratorConfig if it has been initialized.
     * @return GeneratorConfig
     * @throws IllegalStateException throws if GeneratorConfig has not been initialized.
     */
    public static GeneratorConfig getInstance() throws IllegalStateException {
        if(instance == null)
            throw new IllegalStateException("GeneratorConfig not initialized!!!");
        return instance;
    }

    public HashSet<String> getExcludedDirectoryRule() {
        return excludedDirectoryRule;
    }

    public HashSet<String> getExcludedFileRule() {
        return excludedFileRule;
    }

    public boolean isFileExcluded(File file) {
        boolean excluded = false;
        for (String rule : excludedFileRule) {
            if (StringTool.wildcardMatch(rule, file.getName()))
                excluded = true;
        }
        return excluded;
    }

    public boolean isDirectoryExcluded(File dir) {
        boolean excluded = false;
        try {
            for (String rule : excludedDirectoryRule) {
                if (StringTool.wildcardMatch(rule, FileTool.getRelativePath(basePath, dir.getCanonicalPath())))
                    excluded = true;
            }
        } catch (IOException ex) {
            Logger.getGlobal().info("Failed to detect whether directory is excluded: " + dir.getName());
            ex.printStackTrace();
        }
        return excluded;
    }

    private GeneratorConfig(String basePath, File file) throws IOException {
        this.file = file;
        basePath = basePath.replace('\\','/');
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";
        scan();
    }

    private void scan() throws IOException {
        String jsonText = FileTool.readAllText(file.getAbsolutePath());
        JSONTokener jsonTokener = new JSONTokener(jsonText);
        JSONObject json = new JSONObject(jsonTokener);
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
                        Logger.getGlobal().log(Level.INFO, "Declaring a not-found or invalid file " + dir + ". Skip...");
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