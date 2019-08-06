//--------------------------------------------------
// Interface UpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import java.io.File
import java.io.IOException

interface UpdateApplier {
    @Throws(IOException::class)
    fun getApplierTemplate(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File): String

    fun getApplierFileName(context: SelfUpdateApplier): String

    @Throws(IOException::class)
    fun startApplier(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File, applierFile: File): Process
}