//--------------------------------------------------
// Class OutputJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsondata;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

@FunctionalInterface
public interface InJsonData {
    MetaResult decode(String basePath, String data) throws IOException;
}
