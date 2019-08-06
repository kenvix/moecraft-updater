//--------------------------------------------------
// Class WindowsUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import net.moecraft.generator.BuildConfig
import net.moecraft.generator.Environment
import java.io.File

class WindowsUpdateApplier : UpdateApplier {
    override fun getApplierFileName(context: SelfUpdateApplier): String {
        return "self_update_from${BuildConfig.VERSION_CODE}.bat"
    }

    override fun getApplierTemplate(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File): String {
        val targetFilePath = currentJarFile.canonicalPath
        val newFilePath = newJarFile.canonicalPath

        return """
@ECHO OFF
title Updating ${BuildConfig.APPLICATION_NAME}
cd /D ..
echo Killing Current VM (Old version ${BuildConfig.VERSION_NAME})
taskkill /PID ${Environment.getJvmPid()}
ping 127.0.0.1 -n 2 > nul
echo Updating File
copy /Y "$targetFilePath" "$targetFilePath.old"
del /f "$targetFilePath"
copy /Y "$newFilePath" "$targetFilePath"
del /f "$newFilePath"
echo Restarting JVM
"${Environment.getJvmPath(true)}" -jar "$targetFilePath"
ping 127.0.0.1 -n 3 > nul
del /f "%~f0"
        """.trimIndent()
    }

    override fun startApplier(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File, applierFile: File): Process {
        return ProcessBuilder()
                .command(applierFile.canonicalPath)
                .directory(Environment.getUpdaterPath().toFile())
                .start()
    }
}