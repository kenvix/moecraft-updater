//--------------------------------------------------
// Class RepoNetworkUtil
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import com.zhan_dui.download.DownloadManager;
import com.zhan_dui.download.DownloadMission;
import net.moecraft.generator.Environment;
import net.moecraft.generator.meta.FileNode;
import net.moecraft.generator.updater.update.event.OnDownloadMissionFinished;
import net.moecraft.generator.updater.update.event.OnDownloadMissionReady;
import net.moecraft.generator.updater.update.event.OnDownloadProgressChanged;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    /**
     * Download a filenode's all object.
     * @param file FileNode to download
     */
    public void downloadObjects(@NotNull FileNode file, @Nullable OnDownloadProgressChanged onDownloadProgressChanged, @Nullable OnDownloadMissionFinished onDownloadMissionFinished, @Nullable OnDownloadMissionReady onReady) throws IOException {
        downloadObjects(file.getObjects(), onDownloadProgressChanged, onDownloadMissionFinished, onReady);
    }

    public void downloadObjects(@NotNull List<FileNode> objects, @Nullable OnDownloadProgressChanged onDownloadProgressChanged, @Nullable OnDownloadMissionFinished onDownloadMissionFinished, @Nullable OnDownloadMissionReady onReady) throws IOException {
        DownloadManager downloadManager = DownloadManager.getInstance();

        for (FileNode object: objects) {
            if(!object.getFile().exists() || !object.getMD5().equals(object.getExpectedMd5())) {
                DownloadMission mission = new DownloadMission(getRepoFileURL(object).toString(), Environment.getCachePath().toString(), object.getFile().getName());
                downloadManager.addMission(mission);

                if(onReady != null)
                    onReady.accept(mission, downloadManager, object);

                downloadManager.start();

                while (!mission.isFinished()) {
                    if(onDownloadProgressChanged != null)
                        onDownloadProgressChanged.accept(mission, object);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {}
                }

                if(onDownloadMissionFinished != null)
                    onDownloadMissionFinished.accept(mission.getDownloadStatus(), mission, object);
            }
        }
    }
}
