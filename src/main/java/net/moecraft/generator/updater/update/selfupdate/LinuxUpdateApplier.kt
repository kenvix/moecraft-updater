//--------------------------------------------------
// Class LinuxUpdateApplier
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import java.io.File

class LinuxUpdateApplier : UpdateApplier {
    override fun getApplierTemplate(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplierFileName(context: SelfUpdateApplier): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startApplier(context: SelfUpdateApplier, currentJarFile: File, newJarFile: File, applierFile: File): Process {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}