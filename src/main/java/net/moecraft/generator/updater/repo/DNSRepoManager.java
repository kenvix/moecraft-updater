//--------------------------------------------------
// Class DNSRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.Environment;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xbill.DNS.*;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Logger;

public class DNSRepoManager implements RepoManager {

    @Override
    @SuppressWarnings ("unchecked")
    public Repo[] getRepos() throws Exception {
        TreeSet<Repo> repos  = new TreeSet<>();
        final Lookup  lookup = new Lookup(Environment.getDnsRepoDomain(), Type.TXT);
        lookup.setResolver(new SimpleResolver());
        lookup.setCache(null);
        final Record[] records = lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            final StringBuilder builder = new StringBuilder();
            for (Record record : records) {
                try {
                    final TXTRecord txt    = (TXTRecord) record;
                    StringBuilder   buffer = new StringBuilder();
                    txt.getStrings().forEach(buffer::append);
                    String content = new String(Base64.getDecoder().decode(buffer.toString()), StandardCharsets.UTF_8);
                    String[] data = content.split("\\|\\|");
                    int repoOrder = Integer.parseInt(data[0]);
                    Repo repo = new Repo(repoOrder, data[1], data[2], data[3]);
                    repos.add(repo);
                } catch (IndexOutOfBoundsException ex) {
                    Logger.getGlobal().info("Detected invalid Repo: " + ex.getMessage());
                }
            }
        } else {
            throw new Exception("fetch repos failed");
        }
        return repos.toArray(new Repo[repos.size()]);
    }

}
