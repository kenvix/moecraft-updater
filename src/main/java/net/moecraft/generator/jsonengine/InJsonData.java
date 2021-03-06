//--------------------------------------------------
// Class OutputJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

@FunctionalInterface
public interface InJsonData {
    MetaResult decode(String data) throws IOException;
}
