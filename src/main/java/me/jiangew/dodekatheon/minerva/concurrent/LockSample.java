package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

import static me.jiangew.dodekatheon.minerva.concurrent.ConcurrentUtils.sleep;
import static me.jiangew.dodekatheon.minerva.concurrent.ConcurrentUtils.stop;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 18/12/2017
 */
public class LockSample {

    private static final int NUM_INCREMENTS = 10000;
    private static int count = 0;
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        testLock();
    }

    private static void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    private static void testLock() {
        count = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, NUM_INCREMENTS).forEach(i -> executor.submit(LockSample::increment));

        ConcurrentUtils.stop(executor);
        System.out.println("count: " + count);
    }

    private static void testLock2() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ReentrantLock lock = new ReentrantLock();

        executor.submit(() -> {
            lock.lock();
            try {
                sleep(1);
            } finally {
                lock.unlock();
            }
        });

        executor.submit(() -> {
            System.out.println("Locked: " + lock.isLocked());
            System.out.println("Held by me: " + lock.isHeldByCurrentThread());

            boolean locked = lock.tryLock();
            System.out.println("Lock acquired: " + locked);
        });

        ConcurrentUtils.stop(executor);
    }

    private static void testRWLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Map<String, String> map = new HashMap<>();

        ReadWriteLock lock = new ReentrantReadWriteLock();

        executor.submit(() -> {
            lock.writeLock().lock();
            try {
                sleep(1);
                map.put("beats", "solo");
            } finally {
                lock.writeLock().unlock();
            }
        });

        Runnable readTask = () -> {
            lock.readLock().lock();
            try {
                System.out.println("mapValue: " + map.get("beats"));
                sleep(1);
            } finally {
                lock.readLock().unlock();
            }
        };
        executor.submit(readTask);
        executor.submit(readTask);

        ConcurrentUtils.stop(executor);
    }

    private static void teskStampedLock() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Map<String, String> map = new HashMap<>();

        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                sleep(1);
                map.put("beats", "solo");
            } finally {
                lock.unlockWrite(stamp);
            }
        });

        Runnable readTask = () -> {
            long stamp = lock.readLock();
            try {
                System.out.println("mapValue: " + map.get("beats"));
                sleep(1);
            } finally {
                lock.unlockRead(stamp);
            }
        };
        executor.submit(readTask);
        executor.submit(readTask);

        ConcurrentUtils.stop(executor);
    }

    private static void testStampedLock2() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            // 乐观读锁
            long stamp = lock.tryOptimisticRead();
            try {
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                sleep(1);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                sleep(2);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                // 检查发出乐观读锁后同时是否有其他写锁发生?
                if (!lock.validate(stamp)) {
                    // 我们再次获得一个读悲观锁
                    stamp = lock.readLock();
                }
            } finally {
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                System.out.println("Write Lock acquired");
                sleep(2);
            } finally {
                lock.unlock(stamp);
                System.out.println("Write done");
            }
        });

        ConcurrentUtils.stop(executor);
    }

    private static void testStampedLock3() {
        count = 0;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        StampedLock lock = new StampedLock();

        executor.submit(() -> {
            // 悲观读锁
            long stamp = lock.readLock();
            try {
                if (count == 0) {
                    stamp = lock.tryConvertToWriteLock(stamp);
                    if (stamp == 0L) {
                        System.out.println("Could not convert to write lock");
                        // 显式释放读锁
                        lock.unlockRead(stamp);
                        // 显式进行写锁
                        stamp = lock.writeLock();
                    }
                    count = 214;
                }
                System.out.println("Count: " + count);
            } finally {
                lock.unlock(stamp);
            }
        });

        ConcurrentUtils.stop(executor);
    }

    private static void testSemaphores() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Semaphore semaphore = new Semaphore(5);

        Runnable task = () -> {
            boolean permit = false;
            try {
                permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                if (permit) {
                    System.out.println("Semaphore acquired ...");
                    sleep(5);
                } else {
                    System.out.println("Could not acquire semaphore ...");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                if (permit) {
                    semaphore.release();
                }
            }
        };

        IntStream.range(0, 10).forEach(i -> executor.submit(task));

        stop(executor);
    }

}
