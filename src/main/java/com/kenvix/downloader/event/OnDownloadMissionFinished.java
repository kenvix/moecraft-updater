//--------------------------------------------------
// Class OnDownloadMissionFinished
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.event;

import com.kenvix.downloader.download.DownloadMission;
import com.kenvix.downloader.download.DownloadStatus;

@FunctionalInterface
public interface OnDownloadMissionFinished {
    void accept(DownloadStatus downloadStatus, DownloadMission mission);
}
