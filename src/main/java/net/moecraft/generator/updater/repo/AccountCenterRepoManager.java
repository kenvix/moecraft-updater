//--------------------------------------------------
// Class AccountCenterRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.Environment;

public class AccountCenterRepoManager implements RepoManager {
    private final String url = "https://accounts.moecraft.net/API/Updater/repo";

    @Override
    public Repo[] getRepos() throws Exception {
        Environment.getLogger().fine("Pulling repos from " + url);



        return new Repo[0];
    }
}
