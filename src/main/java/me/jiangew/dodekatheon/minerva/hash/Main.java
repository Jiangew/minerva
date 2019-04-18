package me.jiangew.dodekatheon.minerva.hash;

public class Main {

    public static void main(String[] args) {
        // Cluster c = createCluster();
        ConsistentCluster c = createCluster();

        Entry[] entries = {
                new Entry("i"),
                new Entry("have"),
                new Entry("a"),
                new Entry("pen"),
                new Entry("an"),
                new Entry("apple"),
                new Entry("applepen"),
                new Entry("pineapple"),
                new Entry("pineapplepen"),
                new Entry("PPAP")
        };

        for (Entry e : entries) {
            c.put(e);
        }

        c.addServer(new Server("instagram"));
        findEntries(c, entries);
    }

    private static ConsistentCluster createCluster() {
        // Cluster c = new Cluster();
        ConsistentCluster c = new ConsistentCluster();

        c.addServer(new Server("iamjay"));
        c.addServer(new Server("ualili"));
        c.addServer(new Server("appleiphone"));
        c.addServer(new Server("appleimac"));
        c.addServer(new Server("googlepixs"));
        c.addServer(new Server("facebookins"));

        return c;
    }

    private static void findEntries(ConsistentCluster c, Entry[] entries) {
        for (Entry e : entries) {
            if (e == c.get(e)) {
                System.out.println("Found Entry: " + e);
            } else {
                System.out.println("Entry Expired: " + e);
            }
        }
    }

}
