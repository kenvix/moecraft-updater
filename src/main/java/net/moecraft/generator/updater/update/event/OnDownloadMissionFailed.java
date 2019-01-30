//--------------------------------------------------
// Interface OnDownloadMissionFailed
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.event;

import net.moecraft.generator.meta.FileNode;

import java.io.IOException;

@FunctionalInterface
public interface OnDownloadMissionFailed {
    /**
     * Mission failed
     * @param failNum
     * @param fileNode FileNode
     * @param exception exception details
     * @return should downloadManager try again?
     */
    boolean accept(int failNum, FileNode fileNode, IOException exception);
}
