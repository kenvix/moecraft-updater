//--------------------------------------------------
// Class GeneratorConfig
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;
import com.kenvix.utils.StringTool;
import net.moecraft.generator.Environment;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratorConfig extends MetaResult {
    private File file;
    private String basePath;
    private Set<String> excludedFileRule      = new HashSet<>();
    private Set<String> excludedDirectoryRule = new HashSet<>();
    private static GeneratorConfig instance              = null;

    /**
     * Get an instance of GeneratorConfig
     * @param file generator_config.json
     * @return GeneratorConfig
     * @throws IOException failed to read file
     */
    public static GeneratorConfig getInstance(File file) throws IOException {
        if(instance == null) {
            synchronized (GeneratorConfig.class) {
                return instance == null ? (instance = new GeneratorConfig(file)) : instance;
            }
        }
        return instance;
    }

    /**
     * Directly get an instance of GeneratorConfig if it has been initialized.
     * @return GeneratorConfig
     * @throws IllegalStateException throws if GeneratorConfig has not been initialized.
     */
    public static GeneratorConfig getInstance() throws IllegalStateException {
        if(instance == null)
            throw new IllegalStateException("Stupid. GeneratorConfig not initialized.");
        return instance;
    }

    public Set<String> getExcludedDirectoryRule() {
        return excludedDirectoryRule;
    }

    public Set<String> getExcludedFileRule() {
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
            Environment.getLogger().info("Failed to detect whether directory is excluded: " + dir.getName());
            ex.printStackTrace();
        }
        return excluded;
    }

    private GeneratorConfig(File file) throws IOException {
        this.basePath = Environment.getBaseMoeCraftPath();
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";
        this.file = file;
        scan();
    }

    private void scan() throws IOException {
        String jsonText = FileTool.readAllText(file.getAbsolutePath());
        JSONTokener jsonTokener = new JSONTokener(jsonText);
        JSONObject json = new JSONObject(jsonTokener);
        setDescription(json.getString("description"));
        setVersion(json.getString("version"));
        setObjectSize(json.getLong("object_size"));
        searchFileItems("synced_dirs", MetaNodeType.SyncedDirectory, json);
        searchFileItems("synced_files", MetaNodeType.SyncedFile, json);
        searchFileItems("default_files", MetaNodeType.DefaultFile, json);
        searchRuleItems("excluded_files", excludedFileRule, json);
        searchRuleItems("excluded_dir", excludedDirectoryRule, json);
    }

    private void searchRuleItems(String key, Set<String> target, JSONObject json) {
        for (Object item : json.getJSONArray("excluded_files")) {
            if(item instanceof String) {
                target.add(((String) item).replace('\\', '/'));
            } else {
                Environment.getLogger().info("Detected invalid config item " + key + " on " + target.getClass().getName());
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
                        Environment.getLogger().log(Level.INFO, "Declaring a not-found or invalid file " + dir + ". Skip...");
                    }
                } else {
                    Environment.getLogger().info("Detected invalid config item " + key + " on " + type.name());
                }
            }
        } catch (NoSuchFieldException ex) {
            Environment.getLogger().log(Level.CONFIG, "Declaring a invalid field [" + ex.getMessage() + "] Skip...");
        }
    }

    public File getFile() {
        return file;
    }
}
