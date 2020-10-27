//--------------------------------------------------
// Class GeneratorConfig
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import com.kenvix.utils.FileTool;
import com.kenvix.utils.StringTool;
import net.moecraft.generator.BuildConfig;
import net.moecraft.generator.Environment;
import net.moecraft.generator.updater.update.selfupdate.UpdaterInfo;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public final class GeneratorConfig extends MetaResult implements Serializable {
    private JSONObject json;
    private String basePath;
    private String nameRule;
    private String outputJsonName;
    private String moecraftDirectory;
    private Set<String> excludedFileRule = new HashSet<>();
    private Set<String> excludedDirectoryRule = new HashSet<>();
    private static GeneratorConfig instance = null;

    /**
     * FORCE Initialize GeneratorConfig and discard old one.
     *
     * @param file generator_config.json
     * @throws IOException failed to read file
     */
    public synchronized static GeneratorConfig initialize(File file) throws IOException {
        if (instance != null)
            instance = null;

        return instance = new GeneratorConfig(file);
    }

    /**
     * FORCE Initialize GeneratorConfig and discard old one.
     *
     * @param jsonText generator_config.json
     */
    public synchronized static GeneratorConfig initialize(String jsonText) {
        if (instance != null)
            instance = null;

        return instance = new GeneratorConfig(jsonText);
    }

    /**
     * FORCE Initialize GeneratorConfig and discard old one.
     *
     * @param jsonObject generator_config.json
     */
    public synchronized static GeneratorConfig initialize(JSONObject jsonObject) {
        if (instance != null)
            instance = null;

        return instance = new GeneratorConfig(jsonObject);
    }

    /**
     * Directly get an instance of GeneratorConfig if it has been initialized.
     *
     * @return GeneratorConfig
     * @throws IllegalStateException throws if GeneratorConfig has not been initialized.
     */
    public static GeneratorConfig getInstance() throws IllegalStateException {
        if (instance == null)
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
        for (String rule : excludedFileRule)
            if ((new File(basePath, rule).getAbsolutePath().equals(file.getAbsolutePath())))
                excluded = true;
        return excluded;
    }

    public boolean isDirectoryExcluded(File dir) {
        boolean excluded = false;
        for (String rule : excludedDirectoryRule)
            if ((new File(basePath, rule).getAbsolutePath().equals(dir.getAbsolutePath())))
                excluded = true;
        return excluded;
    }

    private GeneratorConfig(File file) throws IOException {
        this(FileTool.readAllText(file.getAbsolutePath()));
    }

    private GeneratorConfig(String jsonText) {
        this(new JSONObject(new JSONTokener(jsonText)));
    }

    private GeneratorConfig(JSONObject jsonObject) {
        this.json = jsonObject;
        this.basePath = Environment.getBaseMoeCraftPath();
        this.basePath = basePath.endsWith("/") ? basePath : basePath + "/";

        UpdaterInfo updaterInfo = new UpdaterInfo(BuildConfig.VERSION_CODE);
        updaterInfo.setVersionName(BuildConfig.VERSION_NAME);

        //Self update is only available for jar build
        if (!System.getProperty("java.class.path").contains(";")) {
            updaterInfo.setObjectFile(new FileNode(new File(System.getProperty("java.class.path")), true));
        }

        setUpdaterInfo(updaterInfo);
        setDescription(json.getString("description"));
        setVersion(json.getString("version"));
        setNameRule(json.getString("name_rule"));
        setObjectSize(json.getLong("object_size"));

        if (Environment.getCommandLine().hasOption('o'))
            setOutputJsonName(Environment.getCommandLine().getOptionValue('o'));
        else
            setOutputJsonName(json.has("output_json") ? json.getString("output_json") : "moecraft.json");

        setMoecraftDirectory(json.has("moecraft_dir") ? json.getString("moecraft_dir") : Environment.getBaseMoeCraftDir().getPath());
    }

    public String getMoecraftDirectory() {
        return moecraftDirectory;
    }

    public GeneratorConfig setMoecraftDirectory(String moecraftDirectory) {
        this.moecraftDirectory = moecraftDirectory;
        return this;
    }

    public void startScan() {
        searchFileItems("synced_dirs", MetaNodeType.SyncedDirectory, json);
        searchFileItems("synced_files", MetaNodeType.SyncedFile, json);
        searchFileItems("default_files", MetaNodeType.DefaultFile, json);
        searchFileItems("default_dirs", MetaNodeType.DefaultDirectory, json);
        searchRuleItems("excluded_files", excludedFileRule, json);
        searchRuleItems("excluded_dirs", excludedDirectoryRule, json);
    }

    public JSONObject getJsonObject() {
        return json;
    }

    private void searchRuleItems(String key, Set<String> target, JSONObject json) {
        for (Object item : json.getJSONArray(key)) {
            if (item instanceof String) {
                target.add(((String) item).replace('\\', '/'));
            } else {
                if (!Environment.isUpdater())
                    Environment.getLogger().info("Detected invalid config item " + key + " on " + target.getClass().getName());
            }
        }
    }

    private void searchFileItems(String key, MetaNodeType type, JSONObject json) {
        try {
            boolean isDirectory = type.getClass().getField(type.name()).isAnnotationPresent(DirectoryMetaNode.class);
            boolean isFile = type.getClass().getField(type.name()).isAnnotationPresent(FileMetaNode.class);
            for (Object dir : json.getJSONArray(key)) {
                if (dir instanceof String) {
                    File dirFile = new File(basePath + dir);
                    if (dirFile.exists()) {
                        if (isDirectory && !dirFile.isFile()) {
                            DirectoryNode directoryNode = new DirectoryNode(dirFile);
                            addDirectoryNode(type, directoryNode);
                        } else if (isFile && dirFile.isFile()) {
                            FileNode fileNode = new FileNode(dirFile);
                            addFileNode(type, fileNode);
                        }
                    } else {
                        if (!Environment.isUpdater())
                            Environment.getLogger().log(Level.INFO, "Declaring a not-found or invalid file " + dir + ". Skip...");
                    }
                } else {
                    if (!Environment.isUpdater())
                        Environment.getLogger().info("Detected invalid config item " + key + " on " + type.name());
                }
            }
        } catch (NoSuchFieldException ex) {
            if (!Environment.isUpdater())
                Environment.getLogger().log(Level.CONFIG, "Declaring a invalid field [" + ex.getMessage() + "] Skip...");
        }
    }

    public String getOutputJsonName() {
        return outputJsonName;
    }

    public GeneratorConfig setOutputJsonName(String outputJsonName) {
        this.outputJsonName = outputJsonName;
        return this;
    }

    private void setNameRule(String nameRule) {
        this.nameRule = nameRule;
    }

    public String getNameRule() {
        return nameRule;
    }
}
