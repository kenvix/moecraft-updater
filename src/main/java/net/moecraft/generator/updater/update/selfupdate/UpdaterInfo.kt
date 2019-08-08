//--------------------------------------------------
// Class UpdaterInfo
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.selfupdate

import net.moecraft.generator.meta.FileNode

data class UpdaterInfo(val versionCode: Int) {
    var versionName: String = "Unknown"
    var objectFile: FileNode? = null
}