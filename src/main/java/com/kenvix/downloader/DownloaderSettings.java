//--------------------------------------------------
// Class DownloaderSettings
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader;

import com.kenvix.downloader.event.*;

public class DownloaderSettings {
    private int downloadThreadNum = 1;
    private boolean overwriteFile = false;
    private OnDownloadMissionPrepared downloadMissionPreparedHandler = null;
    private OnDownloadMissionFailed downloadMissionFailedHandler = null;
    private OnDownloadMissionFinished onDownloadMissionFinished = null;
    private OnDownloadProgressChanged onDownloadProgressChanged = null;
    private OnDownloadMissionReady onDownloadMissionReady = null;

    public int getDownloadThreadNum() {
        return downloadThreadNum;
    }

    public DownloaderSettings setDownloadThreadNum(int downloadThreadNum) {
        this.downloadThreadNum = downloadThreadNum;
        return this;
    }

    public OnDownloadMissionPrepared getDownloadMissionPreparedHandler() {
        return downloadMissionPreparedHandler;
    }

    public DownloaderSettings setDownloadMissionPreparedHandler(OnDownloadMissionPrepared downloadMissionPreparedHandler) {
        this.downloadMissionPreparedHandler = downloadMissionPreparedHandler;
        return this;
    }

    public OnDownloadMissionFailed getDownloadMissionFailedHandler() {
        return downloadMissionFailedHandler;
    }

    public DownloaderSettings setDownloadMissionFailedHandler(OnDownloadMissionFailed downloadMissionFailedHandler) {
        this.downloadMissionFailedHandler = downloadMissionFailedHandler;
        return this;
    }

    public OnDownloadMissionFinished getOnDownloadMissionFinished() {
        return onDownloadMissionFinished;
    }

    public DownloaderSettings setOnDownloadMissionFinished(OnDownloadMissionFinished onDownloadMissionFinished) {
        this.onDownloadMissionFinished = onDownloadMissionFinished;
        return this;
    }

    public OnDownloadProgressChanged getOnDownloadProgressChanged() {
        return onDownloadProgressChanged;
    }

    public DownloaderSettings setOnDownloadProgressChanged(OnDownloadProgressChanged onDownloadProgressChanged) {
        this.onDownloadProgressChanged = onDownloadProgressChanged;
        return this;
    }

    public OnDownloadMissionReady getOnDownloadMissionReady() {
        return onDownloadMissionReady;
    }

    public DownloaderSettings setOnDownloadMissionReady(OnDownloadMissionReady onDownloadMissionReady) {
        this.onDownloadMissionReady = onDownloadMissionReady;
        return this;
    }

    public boolean isOverwriteFile() {
        return overwriteFile;
    }

    public DownloaderSettings setOverwriteFile(boolean overwriteFile) {
        this.overwriteFile = overwriteFile;
        return this;
    }
}
