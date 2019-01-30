//--------------------------------------------------
// Class DownloadMission
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.download;

import com.kenvix.downloader.exception.ServerNotSupportedException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class DownloadMission {
    private URL url;
    private Path savePath;
    private DownloaderSettings settings;
    private DownloadStatus status = DownloadStatus.InitializingDownloader;
    private long fileSize = -1;

    DownloadMission(DownloaderSettings settings, URL url, Path savePath) {
        this.settings = settings;
        this.url = url;
        this.savePath = savePath;
    }


    public boolean start() throws ServerNotSupportedException, IOException {

    }


    public URL getUrl() {
        return url;
    }

    public Path getSavePath() {
        return savePath;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public long getFileSize() {
        return fileSize;
    }

    public DownloadMission setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }
}
