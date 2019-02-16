//--------------------------------------------------
// Class DownloadMetaEvent
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.events;

public class DownloadMetaEvent extends UpdateChildEvent {
    private DownloadEventType downloadEventType;
    private int triedNum;

    public DownloadMetaEvent(DownloadEventType downloadEventType, int triedNum) {
        this.downloadEventType = downloadEventType;
        this.triedNum = triedNum;
    }

    public DownloadMetaEvent(DownloadEventType downloadEventType) {
        this.downloadEventType = downloadEventType;
    }

    public DownloadEventType getDownloadEventType() {
        return downloadEventType;
    }

    public int getTriedNum() {
        return triedNum;
    }
}
