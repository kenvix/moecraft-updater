//--------------------------------------------------
// Class LinuxUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import net.moecraft.generator.BuildConfig
import net.moecraft.generator.Environment
import java.io.File

class LinuxUpdateApplier : UpdateApplier {
    override fun getApplierFileName(context: SelfUpdateApplier): String {
        return "self_update_from${BuildConfig.VERSION_CODE}.sh"
    }

    override fun getApplierTemplate(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File): String {
        val targetFilePath = currentJarFile.canonicalPath
        val newFilePath = newJarFile.canonicalPath

        return """
#!/bin/sh
cd ..
echo "Killing Current VM (Old version ${BuildConfig.VERSION_NAME})"
kill ${Environment.getJvmPid()}
sleep 1s
echo "Updating File"
cp -f -v "$targetFilePath" "$targetFilePath.old"
rm -f "$targetFilePath"
cp -f -v "$newFilePath" "$targetFilePath"
rm -f "$newFilePath"
echo "Restarting JVM"
"${Environment.getJvmPath(true)}" -jar "$targetFilePath"
sleep 2s
rm -- "$0"
        """.trimIndent().replace("\r\n", "\n")
    }

    override fun startApplier(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File, applierFile: File): Process {
        return ProcessBuilder()
                .command(applierFile.canonicalPath)
                .directory(Environment.getUpdaterPath().toFile())
                .start()
    }
}