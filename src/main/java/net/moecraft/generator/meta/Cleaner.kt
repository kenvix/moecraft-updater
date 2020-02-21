//--------------------------------------------------
// Class Cleaner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta

import java.io.File
import java.nio.file.Path

class Cleaner(val fromResult: MetaResult, val directoryPath: Path) {
    fun listUnusedObjects(): List<File> {
        TODO()
    }

    fun deleteUnusedFiles(list: List<File>, onDelete: ((File) -> Unit)? = null) {
        TODO()
    }
}