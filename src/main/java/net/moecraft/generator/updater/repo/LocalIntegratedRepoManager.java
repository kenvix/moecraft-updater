//--------------------------------------------------
// Class LocalIntegratedRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

/**
 * LocalIntegratedRepoManager
 * Fallback into this manager if all methods failed.
 */
public class LocalIntegratedRepoManager implements RepoManager {
    @Override
    public Repo[] getRepos() throws Exception {
        return new Repo[]{
                new Repo(0, "https://cdn.moecraft.net/object/", "moecraft", "moecraft.json", "[推荐] MoeCraft CDN"),
                //new Repo(1, "https://gitlab.com/Kenvix/moxbin/raw/master/object/", "gitlab", "moecraft.json", "GitLab 国外节点")
        };
    }
}
