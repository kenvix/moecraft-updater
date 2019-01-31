//--------------------------------------------------
// Class DownloadMission
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.download;

import com.kenvix.downloader.DownloaderSettings;
import com.kenvix.downloader.exception.ServerNotSupportedException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class DownloadMission {
    private URL url;
    private Path savePath;
    private DownloaderSettings settings;
    private DownloadStatus status = DownloadStatus.InitializingDownloader;
    private long fileSize = -1;
    private int bufferSize = 1024000;
    private RandomAccessFile localFile;

    DownloadMission(DownloaderSettings settings, URL url, Path savePath) {
        this.settings = settings;
        this.url = url;
        this.savePath = savePath;
    }


    public void start() throws ServerNotSupportedException, IOException {
        if(savePath.toFile().exists()) {
            if (settings.isOverwriteFile()) {
                if (!savePath.toFile().delete())
                    throw new IOException("Unable to delete exist file.");
                else
                    throw new FileAlreadyExistsException("Local File Already Exists! If you want to overwrite this file, please use setOverwriteFile()");
            }
        }



        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
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

    private DownloadMission setStatus(DownloadStatus status) {
        this.status = status;
        return this;
    }
}
