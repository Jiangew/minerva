package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 18/12/2017
 */
public class SynchronizedSample {

    private static final int NUM_INCREMENTS = 10000;

    private static int count = 0;

    public static void main(String[] args) {
        testIncrement();
        testIncrementSync();
        testSyncIncrement();
    }

    private static void testIncrementSync() {
        count = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(SynchronizedSample::incrementSync));

        ConcurrentUtils.stop(executor);
        System.out.println("With Sync: " + count);
    }

    private static void testSyncIncrement() {
        count = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(SynchronizedSample::syncIncrement));

        ConcurrentUtils.stop(executor);
        System.out.println("With Class Sync: " + count);
    }

    private static void testIncrement() {
        count = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(SynchronizedSample::increment));

        ConcurrentUtils.stop(executor);
        System.out.println("None Sync: " + count);
    }

    private static void increment() {
        count = count + 1;
    }

    private static synchronized void incrementSync() {
        count = count + 1;
    }

    private static void syncIncrement() {
        synchronized(SynchronizedSample.class) {
            count = count + 1;
        }
    }
}
