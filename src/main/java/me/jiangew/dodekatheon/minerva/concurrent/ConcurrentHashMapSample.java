package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 17/12/2017
 */
public class ConcurrentHashMapSample {

    public static void main(String[] args) {
        System.out.println("Parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        testReduce();
        testSearch();
    }

    private static void testReduce() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("beats", "solo 3");
        map.putIfAbsent("james", "iworks");
        map.putIfAbsent("c3", "p0");

        String reduced = map.reduce(1, (key, value) -> key + "=" + value, (s1, s2) -> s1 + "," + s2);

        System.out.println(reduced);
    }

    private static void testSearch() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("beats", "solo 3");
        map.putIfAbsent("james", "iworks");
        map.putIfAbsent("c3", "p0");

        System.out.println("\nsearch()\n");

        String result1 = map.search(1, (key, value) -> {
            System.out.println(Thread.currentThread().getName());
            if(key.equals("beats") && value.equals("solo 3")) {
                return key + value;
            }
            return null;
        });

        System.out.println(result1);

        System.out.println("\nsearchValues()\n");

        String result2 = map.searchValues(1, value -> {
            System.out.println(Thread.currentThread().getName());
            if(value.length() > 3) {
                return value;
            }
            return null;
        });

        System.out.println(result2);
    }

    private static void testForEach() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.putIfAbsent("foo", "bar");
        map.putIfAbsent("beats", "solo 3");
        map.putIfAbsent("james", "iworks");
        map.putIfAbsent("c3", "p0");

        map.forEach(1, (key, value) -> System.out.printf("key: %s, value: %s, thread: %s\n", key, value, Thread.currentThread().getName()));

        System.out.println(map.mappingCount());
    }
}
