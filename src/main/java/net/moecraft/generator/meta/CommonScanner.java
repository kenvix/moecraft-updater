//--------------------------------------------------
// Interface CommonScanner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import net.moecraft.generator.meta.DirectoryNode;
import net.moecraft.generator.meta.MetaNodeType;
import net.moecraft.generator.meta.MetaResult;
import net.moecraft.generator.meta.MetaScanner;

import java.io.File;

public interface CommonScanner {
    MetaResult scan(File dir, MetaNodeType type, MetaScanner in);
    DirectoryNode scan(File dir, DirectoryNode parentNode, boolean isRootDirectory);
}
