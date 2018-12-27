//--------------------------------------------------
// Class CommonEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine;

import com.kenvix.utils.FileTool;
import net.moecraft.generator.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class CommonEngine {
    protected static final String basePath = Environment.getBaseMoeCraftPath();

    protected void writeJson(File target, String result) throws IOException {
        if(target.exists())
            if(!target.delete()) {
                Logger.getGlobal().warning("Unable to delete: " + target.getName() + " . Generation failed");
                return;
            }
        FileWriter writer = new FileWriter(target);
        writer.write(result);
        writer.close();
    }

    protected String getRelativePath(String path) {
        return FileTool.getRelativePath(basePath, path);
    }
}
