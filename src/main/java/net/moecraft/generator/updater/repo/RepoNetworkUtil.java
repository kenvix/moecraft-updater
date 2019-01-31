//--------------------------------------------------
// Class RepoNetworkUtil
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.meta.FileNode;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class RepoNetworkUtil {
    private final Repo repo;

    public RepoNetworkUtil(@NotNull Repo repo) {
        this.repo = repo;
    }

    public URL getRepoFileURL(@NotNull String fileName) throws MalformedURLException {
        return new URL(repo.getUrl() + fileName);
    }

    public URL getRepoFileURL(@NotNull FileNode fileNode) throws MalformedURLException {
        return getRepoFileURL(fileNode.getFile().getName());
    }

    public String downloadRepoMetaAsString() throws IOException {
        return downloadString(getRepoFileURL(repo.getMetaFileName()));
    }

    public void simpleDownloadFile(URL url, Path savePath) throws IOException {
        if(savePath.toFile().exists()) {
            if(!savePath.toFile().delete())
                throw new IOException("Unable to delete exist file");
        }

        FileUtils.copyURLToFile(url, savePath.toFile(), 10, 10000);
    }

    @NotNull
    public static String downloadString(@NotNull URL url) throws IOException {
        StringBuilder data = new StringBuilder();
        URLConnection urlConnection = url.openConnection();

        InputStream networkInput = url.openStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = networkInput.read(buffer)) != -1) {
            data.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
        }

        return data.toString();
    }

//    /**
//     * Download a filenode's all object.
//     * @param file FileNode to DownloaderSettings
//     */
//    public boolean downloadObjects(@NotNull FileNode file, @Nullable OnDownloadProgressChanged onDownloadProgressChanged, @Nullable OnDownloadMissionFailed onDownloadMissionFailed, @Nullable OnDownloadMissionFinished onDownloadMissionFinished, @Nullable OnDownloadMissionReady onReady)  {
//        return downloadObjects(file.getObjects(), onDownloadProgressChanged, onDownloadMissionFailed, onDownloadMissionFinished, onReady);
//    }
//
//    public boolean downloadObjects(@NotNull List<FileNode> objects, @Nullable OnDownloadProgressChanged onDownloadProgressChanged, @Nullable OnDownloadMissionFailed onDownloadMissionFailed, @Nullable OnDownloadMissionFinished onDownloadMissionFinished, @Nullable OnDownloadMissionReady onReady) {
//        for (FileNode object: objects) {
//            if(!object.getFile().exists() || !object.getMD5().equals(object.getExpectedMd5())) {
//
//                try {
//                    Downloader downloader = new Downloader(getRepoFileURL(object).toString(), Environment.getCachePath().resolve(object.getFile().getName()).toString(), 4);
//
//                    downloader.download(new Downloader.CallBack() {
//                        @Override
//                        public void onProgress(int progress) {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            break;
//                        }
//
//                        @Override
//                        public void onFinish(String path) {
//
//                        }
//                    });
//
//                    if(onReady != null)
//                        onReady.accept(mission, downloadManager, object);
//
//                    downloadManager.start();
//
//                    while (!mission.isFinished()) {
//                        if(onDownloadProgressChanged != null)
//                            onDownloadProgressChanged.accept(mission, object);
//
//                        try {
//                            Thread.sleep(50);
//                        } catch (InterruptedException ex) {}
//                    }
//
//                    File downloadedObject = Environment.getCachePath().resolve(object.getFile().getName()).toFile();
//
//                    String downloadFile = FileTool.getFileMD5(downloadedObject);
//                    if(downloadedObject.length() != object.getSize() || downloadFile == null || !downloadFile.equals(object.getExpectedMd5()))
//                        throw new IOException("Downloaded file is broken.");
//
//                    //if(onDownloadMissionFinished != null)
//                    //    onDownloadMissionFinished.accept(mission.getDownloadStatus(), mission, object);
//
//                    break;
//                } catch (IOException ex) {
//                    failNum++;
//
//                    if(onDownloadMissionFailed == null)
//                        return false;
//
//                    if(!onDownloadMissionFailed.accept(failNum, object, ex))
//                        break;
//                }
//            }
//        }
//
//        return true;
//    }
}
