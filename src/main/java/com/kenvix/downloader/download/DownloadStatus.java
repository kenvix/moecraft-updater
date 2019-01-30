//--------------------------------------------------
// Enum DownloadStatus
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.download;

public enum DownloadStatus {
    InitializingDownloader,
    Ready,
    PreparingFile,
    Downloading,
    Paused,
    Failed,
    Canceled
}
