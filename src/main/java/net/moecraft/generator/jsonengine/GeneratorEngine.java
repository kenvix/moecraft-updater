//--------------------------------------------------
// Interface GeneratorEngine
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsonengine;

public interface GeneratorEngine extends OutJsonData {
    void save(Object in) throws Exception;
}
