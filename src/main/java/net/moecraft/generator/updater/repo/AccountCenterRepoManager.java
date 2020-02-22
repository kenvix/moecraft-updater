//--------------------------------------------------
// Class AccountCenterRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.Environment;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.net.URL;
import java.util.TreeSet;

public class AccountCenterRepoManager implements RepoManager {

    @Override
    public Repo[] getRepos() throws Exception {
        Environment.getLogger().fine("Pulling repos from " + Environment.getRepoManagerURL());
        TreeSet<Repo> result = new TreeSet<>();

        URL url = new URL(Environment.getRepoManagerURL());
        String data = RepoNetworkUtil.downloadString(url);

        JSONTokener jsonTokener = new JSONTokener(data);
        JSONArray repos = new JSONArray(jsonTokener);

        repos.forEach(repo -> {
            JSONArray repoInfo = new JSONArray(new JSONTokener(repo.toString()));
            result.add(new Repo(
                    repoInfo.getInt(0),
                    repoInfo.getString(1),
                    repoInfo.getString(2),
                    repoInfo.getString(3),
                    repoInfo.getString(4),
                    repoInfo.length() >= 6 ? repoInfo.getString(5) : null
            ));
        });

        return result.toArray(new Repo[0]);
    }
}
