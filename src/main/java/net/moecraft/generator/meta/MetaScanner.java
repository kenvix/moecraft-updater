package net.moecraft.generator.meta;

import net.moecraft.generator.meta.scanner.Scanner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Logger;

public class MetaScanner {
    private MetaResult result = new MetaResult();
    private Scanner scanner;
    private GeneratorConfig config = GeneratorConfig.getInstance();

    public MetaScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Get scanned result
     * Warning: this method does not scan files!!
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
                if(field.isAnnotationPresent(ScanableMetaNode.class)) {
                    if(field.isAnnotationPresent(DirectoryMetaNode.class))
                        config.getDirectoryNodesByType(type).forEach(dir -> scanner.scan(dir.getDirectory(), type, this));
                    else if (field.isAnnotationPresent(FileMetaNode.class))
                        config.getFileNodesByType(type).getFileNodes().forEach(file -> result.addFileNode(type, file));
                }
            } catch (NoSuchFieldException ex) {
                Logger.getGlobal().info("Scanner : no such field: " + ex.getMessage());
            }
        }
        return result;
    }
}
