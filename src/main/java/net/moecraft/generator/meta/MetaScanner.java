package net.moecraft.generator.meta;

import net.moecraft.generator.meta.scanner.Scanner;

import java.io.File;

public class MetaScanner {
    private File dir;
    private MetaResult result;

    public MetaScanner(File dir) {
        this.dir = dir;
        this.result = new MetaResult();
    }

    public File getDir() {
        return dir;
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

    public MetaResult scan(Scanner scanner) {
        for (MetaNodeType type : MetaNodeType.values())
            scanner.scan(dir, type, this);
        return result;
    }
}
