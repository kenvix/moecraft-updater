//--------------------------------------------------
// Class Repo
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

public class Repo implements Comparable<Repo> {
    public String getUrl() {
        return url;
    }

    private String url;
    private String name;

    public int getOrder() {
        return order;
    }

    private int order;

    public String getName() {
        return name;
    }

    public Repo(int order, String url, String name, String description) {
        this.url = url;
        this.description = description;
        this.name = name;
        this.order = order;
    }

    public String getDescription() {
        return description;
    }

    private String description;

    @Override
    public int compareTo(Repo o) {
        return Integer.compare(order, o.order);
    }

    @Override
    public String toString() {
        return name;
    }
}
