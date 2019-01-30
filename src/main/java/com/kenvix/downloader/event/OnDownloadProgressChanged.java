//--------------------------------------------------
// Interface OnDownloadProgressChanged
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.event;

import com.kenvix.downloader.download.DownloadMission;
import net.moecraft.generator.meta.FileNode;

@FunctionalInterface
public interface OnDownloadProgressChanged {
    void accept(DownloadMission mission, FileNode fileNode);
}
