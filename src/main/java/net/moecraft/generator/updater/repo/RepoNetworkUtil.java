//--------------------------------------------------
// Class RepoNetworkUtil
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public final class RepoNetworkUtil {
    private final Repo repo;

    public RepoNetworkUtil(Repo repo) {
        this.repo = repo;
    }

    public URL getRepoFileURL(String fileName) throws MalformedURLException {
        return new URL(repo.getUrl() + fileName);
    }

    public String downloadRepoMetaAsString() throws IOException {
        return downloadString(getRepoFileURL(repo.getMetaFileName()));
    }

    public static String downloadString(URL url) throws IOException {
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
}
