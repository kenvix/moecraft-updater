//--------------------------------------------------
// Class MetaNode
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@interface FileMetaNode {
}

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@interface DirectoryMetaNode {
}

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@interface ScanableMetaNode {
}