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
    @ScanableMetaNode @DirectoryMetaNode DefaultDirectory,
    /**
     * a File should be excluded from Scanner, or should be deleted by Updater
     */
    @FileMetaNode ExcludedFile,
    /**
     * a Directory should be excluded from Scanner, or should be deleted by Updater
     */
    @DirectoryMetaNode ExcludedDirectory,
}
