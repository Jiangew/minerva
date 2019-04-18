package me.jiangew.dodekatheon.minerva.stream;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 19/12/2017
 */
public class StreamsExample {

    public static void main(String[] args) {
        new StreamsExample().testMethod();
        sortSequential();
        sortParallel();

        List<String> list = Arrays.asList("d2", "a2", "b1", "b3", "c");
        testList1(list);
        testList2(list);

        testOperate1();
        testOperate2();
        testOperate3();
        testOperate4();
    }

    private void testMethod() {
        List<String> list = new ArrayList<>();
        list.add("ddd2");
        list.add("aaa2");
        list.add("bbb1");
        list.add("aaa1");
        list.add("bbb3");
        list.add("ccc");
        list.add("bbb2");
        list.add("ddd1");

        // filtering
        list.stream().filter((s) -> s.startsWith("a")).forEach(System.out::println);
        // "aaa2", "aaa1"

        // sorting
        list.stream().sorted().filter((s) -> s.startsWith("a")).forEach(System.out::println);
        // "aaa1", "aaa2"

        // mapping
        list.stream().map(String::toUpperCase).sorted((a, b) -> b.compareTo(a)).forEach(System.out::println);
        // "DDD2", "DDD1", "CCC", "BBB3", "BBB2", "AAA2", "AAA1"

        // matching
        boolean anyStartsWithA = list.stream().anyMatch((s) -> s.startsWith("a"));
        System.out.println(anyStartsWithA);      // true

        boolean allStartsWithA = list.stream().allMatch((s) -> s.startsWith("a"));
        System.out.println(allStartsWithA);      // false

        boolean noneStartsWithZ = list.stream().noneMatch((s) -> s.startsWith("z"));
        System.out.println(noneStartsWithZ);     // true

        // counting
        long startsWithB = list.stream().filter((s) -> s.startsWith("b")).count();
        System.out.println(startsWithB);         // 3

        // reducing
        Optional<String> reduced = list.stream().sorted().reduce((s1, s2) -> s1 + "#" + s2);
        reduced.ifPresent(System.out::println);
        // "aaa1#aaa2#bbb1#bbb2#bbb3#ccc#ddd1#ddd2"
    }

    private static final int MAX = 1000000;

    private static void sortSequential() {
        List<String> values = new ArrayList<>();
        for(int i = 0; i < MAX; i++) {
            values.add(UUID.randomUUID().toString());
        }

        long t0 = System.nanoTime();
        long count = values.stream().sorted().count();
        long t1 = System.nanoTime();

        long cost = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("sequential sort cost: %d ms, count: %s", cost, count));
    }

    private static void sortParallel() {
        List<String> values = new ArrayList<>(MAX);
        for(int i = 0; i < MAX; i++) {
            values.add(UUID.randomUUID().toString());
        }

        long t0 = System.nanoTime();
        long count = values.parallelStream().sorted().count();
        long t1 = System.nanoTime();

        long cost = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
        System.out.println(String.format("parallel sort cost: %d ms, count: %s", cost, count));
    }

    private static void testList1(List<String> list) {
        list.stream().filter(s -> {
            System.out.println("filter:  " + s);
            return s.toLowerCase().startsWith("b");
        }).sorted((s1, s2) -> {
            System.out.printf("sort:    %s; %s\n", s1, s2);
            return s1.compareTo(s2);
        }).map(s -> {
            System.out.println("map:     " + s);
            return s.toUpperCase();
        }).forEach(s -> System.out.println("forEach: " + s));
    }

    // sorted = horizontal
    private static void testList2(List<String> list) {
        list.stream().sorted((s1, s2) -> {
            System.out.printf("sort:    %s; %s\n", s1, s2);
            return s1.compareTo(s2);
        }).filter(s -> {
            System.out.println("filter:  " + s);
            return s.toLowerCase().startsWith("a");
        }).map(s -> {
            System.out.println("map:     " + s);
            return s.toUpperCase();
        }).forEach(s -> System.out.println("forEach: " + s));
    }

    private static void testOperate4() {
        Stream.of(new BigDecimal("1.2"), new BigDecimal("3.7")).mapToDouble(BigDecimal::doubleValue).average().ifPresent(System.out::println);
    }

    private static void testOperate3() {
        IntStream.range(0, 10).average().ifPresent(System.out::println);
    }

    private static void testOperate2() {
        IntStream.builder().add(1).add(3).add(5).add(7).add(11).build().average().ifPresent(System.out::println);
    }

    private static void testOperate1() {
        int[] ints = {1, 3, 5, 7, 11};
        Arrays.stream(ints).average().ifPresent(System.out::println);
    }

}
