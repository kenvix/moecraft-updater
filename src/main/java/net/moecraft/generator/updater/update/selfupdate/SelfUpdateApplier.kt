//--------------------------------------------------
// Class SelfUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import net.moecraft.generator.Environment
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class SelfUpdateApplier(applier: UpdateApplier? = null) {
    private val applier: UpdateApplier
    private val currentJarFile = File(System.getProperty("java.class.path"))
    private lateinit var newJarFile: File

    init {
        if (applier == null) {
            this.applier = if (Environment.isRunningOnWindowsPlatform()) WindowsUpdateApplier() else LinuxUpdateApplier()
        } else {
            this.applier = applier
        }
    }

    fun start(newJarPath: String) {
        this.newJarFile = Environment.getBasePath().resolve(newJarPath).toFile()

        if (!newJarFile.exists())
            throw FileNotFoundException("New jar file not found")

        if (!currentJarFile.exists())
            throw FileNotFoundException("Current jar file not found")

        val applierTemplate: String = applier.getApplierTemplate(this, currentJarFile, newJarFile)
        val applierFile: File = Environment.getUpdaterPath().resolve(applier.getApplierFileName(this)).toFile()

        if (applierFile.exists())
            applierFile.delete()

        applierFile.writeText(applierTemplate)
        val process = applier.startApplier(this, currentJarFile, newJarFile, applierFile)

        process.waitFor()
        exitProcess(0)
    }
}