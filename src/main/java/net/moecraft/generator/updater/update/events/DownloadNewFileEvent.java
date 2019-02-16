//--------------------------------------------------
// Class DownloadMetaEvent
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.update.events;

public class DownloadNewFileEvent extends UpdateChildEvent {
    private DownloadEventType downloadEventType;
    private DownloadEventAction downloadEventAction;
    private int triedNum;

    public DownloadNewFileEvent(DownloadEventType downloadEventType, int triedNum) {
        this.downloadEventType = downloadEventType;
        this.triedNum = triedNum;
    }

    public DownloadNewFileEvent(DownloadEventType downloadEventType) {
        this.downloadEventType = downloadEventType;
    }

    public DownloadNewFileEvent(DownloadEventType downloadEventType, DownloadEventAction downloadEventAction, int triedNum) {
        this.downloadEventType = downloadEventType;
        this.downloadEventAction = downloadEventAction;
        this.triedNum = triedNum;
    }

    public DownloadNewFileEvent(DownloadEventType downloadEventType, DownloadEventAction downloadEventAction) {
        this.downloadEventType = downloadEventType;
        this.downloadEventAction = downloadEventAction;
    }

    public DownloadEventType getDownloadEventType() {
        return downloadEventType;
    }

    public int getTriedNum() {
        return triedNum;
    }
}
