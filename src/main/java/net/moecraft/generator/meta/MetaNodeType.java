//--------------------------------------------------
// Enum MetaNodeType
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

public enum MetaNodeType {
    @DirectoryMetaNode SyncedDirectory,
    @FileMetaNode SyncedFile,
    @FileMetaNode DefaultFile,
    @FileMetaNode ExcludedFile,
    @DirectoryMetaNode ExcludedDirectory,
}
