//--------------------------------------------------
// Interface OnDownloadMissionReady
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.event;

import com.kenvix.downloader.download.DownloadMission;

@FunctionalInterface
public interface OnDownloadMissionPrepared {
    void accept(DownloadMission mission);
}
