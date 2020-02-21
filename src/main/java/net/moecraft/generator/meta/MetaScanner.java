package net.moecraft.generator.meta;

import net.moecraft.generator.Environment;
import net.moecraft.generator.meta.scanner.CommonScanner;

import java.lang.reflect.Field;

public class MetaScanner {
    private MetaResult result = new MetaResult();
    private CommonScanner scanner;
    private GeneratorConfig config;

    public MetaScanner(CommonScanner scanner) {
        this.scanner = scanner;
    }

    public MetaScanner(CommonScanner scanner, GeneratorConfig config) {
        this.scanner = scanner;
        this.config = config;
    }

    /**
     * Get scanned result
     * Warning: this method does not scan files!!
     *
     * @return result
     */
    public MetaResult getResult() {
        return result;
    }

    public MetaScanner setResult(MetaResult result) {
        this.result = result;
        return this;
    }

    public MetaResult scan() {
        for (MetaNodeType type : MetaNodeType.values()) {
            try {
                Field field = type.getClass().getField(type.name());
                if (field.isAnnotationPresent(ScanableMetaNode.class)) {
                    if (field.isAnnotationPresent(DirectoryMetaNode.class))
                        config.getDirectoryNodesByType(type).forEach(dir -> scanner.scan(dir.getDirectory(), type, this));
                    else if (field.isAnnotationPresent(FileMetaNode.class))
                        config.getFileNodesByType(type).getFileNodes().forEach(file -> result.addFileNode(type, file));
                }
            } catch (NoSuchFieldException ex) {
                Environment.getLogger().info("CommonScanner : no such field: " + ex.getMessage());
            }
        }
        return result;
    }
}
