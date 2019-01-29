//--------------------------------------------------
// Class AccountCenterRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.Environment;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;

public class AccountCenterRepoManager implements RepoManager {

    @Override
    public Repo[] getRepos() throws Exception {
        Environment.getLogger().fine("Pulling repos from " + Environment.getRepoManagerURL());

        StringBuilder data = new StringBuilder();
        TreeSet<Repo> result = new TreeSet<>();

        URL url = new URL(Environment.getRepoManagerURL());
        URLConnection urlConnection = url.openConnection();

        InputStream networkInput = url.openStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = networkInput.read(buffer)) != -1) {
            data.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
        }

        JSONTokener jsonTokener = new JSONTokener(data.toString());
        JSONArray repos = new JSONArray(jsonTokener);

        repos.forEach(repo -> {
            JSONArray repoInfo = new JSONArray(new JSONTokener(repo.toString()));
            result.add(new Repo(repoInfo.getInt(0), repoInfo.getString(1), repoInfo.getString(2), repoInfo.getString(3), repoInfo.getString(4)));
        });

        return result.toArray(new Repo[0]);
    }
}
