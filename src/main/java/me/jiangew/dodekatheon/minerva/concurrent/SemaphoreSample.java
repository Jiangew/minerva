package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 18/12/2017
 */
public class SemaphoreSample {

    private static final int NUM_INCREMENTS = 10000;

    private static Semaphore semaphore = new Semaphore(1);

    private static Semaphore semaphoreWork = new Semaphore(5);

    private static int count = 0;

    public static void main(String[] args) {
        testIncrement();
        testDoWork();
    }

    private static void testIncrement() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(SemaphoreSample::increment));

        ConcurrentUtils.stop(executor);
        System.out.println("Increment: " + count);
    }

    private static void increment() {
        boolean permit = false;
        try {
            permit = semaphore.tryAcquire(5, TimeUnit.SECONDS);
            count++;
        } catch (InterruptedException e) {
            throw new RuntimeException("could not increment");
        } finally {
            if(permit) {
                semaphore.release();
            }
        }
    }

    private static void testDoWork() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        IntStream.range(0, 10).forEach(i -> executor.submit(SemaphoreSample::doWork));

        ConcurrentUtils.stop(executor);
    }

    private static void doWork() {
        boolean permit = false;
        try {
            permit = semaphoreWork.tryAcquire(1, TimeUnit.SECONDS);
            if(permit) {
                System.out.println("Semaphore acquired");
                ConcurrentUtils.sleep(5);
            } else {
                System.out.println("Could not acquire semaphore");
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            if(permit) {
                semaphoreWork.release();
            }
        }
    }
}
