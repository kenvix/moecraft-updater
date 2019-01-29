//--------------------------------------------------
// Class DNSRepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

import net.moecraft.generator.Environment;
import org.xbill.DNS.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.TreeSet;

public class DNSRepoManager implements RepoManager {

    @Override
    @SuppressWarnings ("unchecked")
    public Repo[] getRepos() throws Exception {
        TreeSet<Repo> repos  = new TreeSet<>();
        final Lookup  lookup = new Lookup(Environment.getDnsRepoDomain(), Type.TXT);

        //lookup.setResolver(new SimpleResolver());
        //lookup.setCache(null);

        Record[] records = null;
        int lookupResult = Lookup.TRY_AGAIN;

        for (int i = -1; i < Environment.getDnsMaxTries() && lookupResult == Lookup.TRY_AGAIN; i++) {
            Environment.getLogger().fine(String.format("Fetching repos by DNS Query (try %d/%d) on %s", i+1, Environment.getDnsMaxTries(), Environment.getDnsRepoDomain()));
            lookup.run();
            records      = lookup.getAnswers();
            lookupResult = lookup.getResult();
        }

        if (lookupResult == Lookup.SUCCESSFUL && records != null) {
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
                    Environment.getLogger().info("Detected invalid Repo: " + ex.getMessage());
                }
            }
        } else {
            throw new IOException("Unable to fetch MoeCraft repos. Check your network connection.");
        }
        return repos.toArray(new Repo[repos.size()]);
    }

}