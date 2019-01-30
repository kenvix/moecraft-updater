//--------------------------------------------------
// Interface OnDownloadProgressChanged
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.event;

import com.zhan_dui.download.DownloadMission;
import net.moecraft.generator.meta.FileNode;

@FunctionalInterface
public interface OnDownloadProgressChanged {
    void accept(DownloadMission mission, FileNode fileNode);
}
