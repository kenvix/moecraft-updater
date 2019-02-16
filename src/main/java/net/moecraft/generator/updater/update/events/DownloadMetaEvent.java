//--------------------------------------------------
// Class DownloadMetaEvent
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.events;

public class DownloadMetaEvent extends UpdateChildEvent {
    private DownloadMetaEventType downloadMetaEventType;
    private int triedNum;

    public DownloadMetaEvent(DownloadMetaEventType downloadMetaEventType, int triedNum) {
        this.downloadMetaEventType = downloadMetaEventType;
        this.triedNum = triedNum;
    }

    public DownloadMetaEvent(DownloadMetaEventType downloadMetaEventType) {
        this.downloadMetaEventType = downloadMetaEventType;
    }

    public DownloadMetaEventType getDownloadMetaEventType() {
        return downloadMetaEventType;
    }

    public int getTriedNum() {
        return triedNum;
    }
}
