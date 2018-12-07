//--------------------------------------------------
// Interface Scanner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta.scanner;

import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;

import java.io.File;

@FunctionalInterface
public interface Scanner {
    MetaResult scan(File dir, MetaScanner in);
}
