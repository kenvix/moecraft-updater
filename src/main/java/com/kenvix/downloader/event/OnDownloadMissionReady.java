//--------------------------------------------------
// Interface OnDownloadMissionReady
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.event;

import com.kenvix.downloader.download.DownloadMission;

@FunctionalInterface
public interface OnDownloadMissionReady {
    void accept(DownloadMission mission);
}
