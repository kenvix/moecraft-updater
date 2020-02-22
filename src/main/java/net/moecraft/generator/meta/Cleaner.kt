//--------------------------------------------------
// Class Cleaner
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.meta

import net.moecraft.generator.jsonengine.engine.NewMoeEngine
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*
import java.util.function.Consumer

class Cleaner(val result: MetaResult, val directoryPath: Path) {

    constructor(file: File, directoryPath: Path): this(NewMoeEngine().decode(file.readText(Charset.forName("UTF-8"))), directoryPath)

    fun listUnusedObjects(): List<File> {
        val files = directoryPath.toFile().listFiles()
        val diff = LinkedList<File>()
        val objects = result.globalObjects

        if (files != null) {
            for (file in files) {
                try {
                    if (!objects.containsKey(ObjectEngine.getObjectMd5FromFileName(file.name)))
                        diff.add(file)
                } catch (e: IllegalArgumentException) {}
            }
        }

        return diff
    }

    fun deleteUnusedFiles(list: List<File>, onDelete: Consumer<File>? = null) {
        list.forEach {
            onDelete?.accept(it)
            it.delete()
        }
    }

    fun scanAndDelete(onDelete: Consumer<File>? = null) {
        deleteUnusedFiles(listUnusedObjects(), onDelete)
    }
}