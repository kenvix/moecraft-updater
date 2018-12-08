//--------------------------------------------------
// Class OutputJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.outjson;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

@FunctionalInterface
public interface OutJson {
    String encode(String basePath, MetaResult result) throws IOException;
}
