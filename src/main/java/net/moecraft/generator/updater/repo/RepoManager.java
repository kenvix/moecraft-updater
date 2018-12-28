//--------------------------------------------------
// Interface RepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

public interface RepoManager {
    Repo[] getRepos() throws Exception;
}
