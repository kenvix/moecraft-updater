//--------------------------------------------------
// Enum MetaNodeType
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta;

public enum MetaNodeType {
    @ScanableMetaNode @DirectoryMetaNode SyncedDirectory,
    @ScanableMetaNode @FileMetaNode SyncedFile,
    @ScanableMetaNode @FileMetaNode DefaultFile,
    @FileMetaNode ExcludedFile,
    @DirectoryMetaNode ExcludedDirectory,
}
