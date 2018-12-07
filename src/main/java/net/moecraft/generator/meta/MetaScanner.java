package net.moecraft.generator.meta;

import net.moecraft.generator.meta.scanner.Scanner;

import java.io.File;

public class MetaScanner {
    private File dir;
    private MetaResult result = new MetaResult();
    private MetaNodeType type;

    public MetaScanner(MetaNodeType type, File dir) {
        this.dir = dir;
        this.type = type;
    }

    public File getDir() {
        return dir;
    }

    public MetaResult getResult() {
        return result;
    }

    public MetaScanner setResult(MetaResult result) {
        this.result = result;
        return this;
    }

    public MetaResult scan(Scanner scanner) {
        return scanner.scan(dir, this);
    }

    public MetaNodeType getMetaNodeType() {
        return type;
    }
}
