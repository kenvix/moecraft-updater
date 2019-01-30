//--------------------------------------------------
// Class Downloader
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.downloader.download;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {
    private DownloaderSettings settings;

    public Downloader(DownloaderSettings settings) {
        this.settings = settings;
    }

    public DownloadMission createMission(String url, String path) throws MalformedURLException {
        return new DownloadMission(settings, new URL(url), Paths.get(path));
    }

    public DownloadMission createMission(String url, Path path) throws MalformedURLException {
        return new DownloadMission(settings, new URL(url), path);
    }

    public DownloadMission createMission(URL url, String path) {
        return new DownloadMission(settings, url, Paths.get(path));
    }

    public DownloadMission createMission(URL url, Path path) {
        return new DownloadMission(settings, url, path);
    }
}
