//--------------------------------------------------
// Class OutputJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

@FunctionalInterface
public interface OutJsonData {
    String encode(MetaResult result) throws IOException;
}
