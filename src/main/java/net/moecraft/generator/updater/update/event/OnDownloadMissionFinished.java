//--------------------------------------------------
// Class OnDownloadMissionFinished
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.event;

import com.zhan_dui.download.DownloadMission;
import com.zhan_dui.download.DownloadStatus;
import net.moecraft.generator.meta.FileNode;

@FunctionalInterface
public interface OnDownloadMissionFinished {
    void accept(DownloadStatus downloadStatus, DownloadMission mission, FileNode fileNode);
}
