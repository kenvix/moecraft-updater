//--------------------------------------------------
// Interface GeneratorEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsondata;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

public interface GeneratorEngine extends OutJsonData {
    void save(String basePath, Object in) throws Exception;
}
