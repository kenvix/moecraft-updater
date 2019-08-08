//--------------------------------------------------
// Interface OnDownloadMissionFailed
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.event;

import com.kenvix.downloader.download.DownloadMission;

@FunctionalInterface
public interface OnDownloadMissionFailed {
    /**
     * Mission failed
     *
     * @param mission
     * @param throwable exception details
     */
    boolean accept(DownloadMission mission, Throwable throwable);
}
