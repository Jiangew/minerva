package me.jiangew.dodekatheon.minerva.hash;

public class Entry {

    private String key;

    Entry(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    /**
     * 为了 servers 和 entries 在 hash 环上足够分散，重写 hashCode 方法；简单起见，复用 String 的 hashCode 算法。
     *
     * @return
     */
    public int hashCode() {
        return key.hashCode();
    }

}
