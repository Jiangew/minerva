package me.jiangew.dodekatheon.minerva.hash;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private String name;
    private Map<Entry, Entry> entries;

    Server(String name) {
        this.name = name;
        entries = new HashMap<>();
    }

    public void put(Entry e) {
        entries.put(e, e);
    }

    public Entry get(Entry e) {
        return entries.get(e);
    }

    /**
     * 为了 servers 和 entries 在 hash 环上足够分散，重写 hashCode 方法；简单起见，复用 String 的 hashCode 算法。
     *
     * @return
     */
    public int hashCode() {
        return name.hashCode();
    }

}
