package me.jiangew.dodekatheon.minerva.io;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desc: 扩展 ThreadPoolExecutor
 * <p>
 * ThreadPoolExecutor的执行流程：
 * 1.core线程还能应付的,则不断的创建新的线程；
 * 2.core线程无法应付,则将任务扔到队列里面；
 * 3.队列满了「意味着插入任务失败」,则开始创建MAX线程,线程数达到MAX后,队列还一直是满的,则抛出RejectedExecutionException。
 * <p>
 * 问题：
 * 1.这个执行流程有个小问题,就是当core线程无法应付请求的时候,会立刻将任务添加到队列中,如果队列非常长,而任务又非常多,那么将会有频繁的任务入队列和任务出队列的操作。
 * <p>
 * 根据实际压测发现,这种操作也是有一定消耗的。其实JAVA提供的SynchronousQueue队列是一个零长度的队列,任务都是直接由生产者递交给消费者,中间没有入队列的过程；
 * 可见JAVA API的设计者也是有考虑过入队列这种操作的开销。
 * <p>
 * 2.任务一多,立刻扔到队列里,而MAX线程又不干活,如果队列里面太多任务了,只有可怜的core线程在忙,也是会影响性能的。
 * <p>
 * 当core线程无法应付请求的时候,能不能延后入队列这个操作呢? 让MAX线程尽快启动起来,帮忙处理任务。
 * <p>
 * Author: Jiangew
 * Date: 20/10/2017
 */
public class EnhancedThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 计数器,用于表示已经提交到队列里面的task的数量,这里task特指还未完成的task。
     * 当task执行完后,submittedTaskCount会减1的。
     */
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    public EnhancedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, TaskQueue workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadPoolExecutor.AbortPolicy());
        workQueue.setExecutor(this);
    }

    /**
     * 覆盖父类的afterExecute方法,当task执行完成后,将计数器减1
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        submittedTaskCount.decrementAndGet();
    }

    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    /**
     * 覆盖父类的execute方法,在任务开始执行之前,计数器加1。
     */
    @Override
    public void execute(Runnable command) {
        submittedTaskCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            //当发生RejectedExecutionException,尝试再次将task丢到队列里面,如果还是发生RejectedExecutionException,则直接抛出异常。
            BlockingQueue<Runnable> taskQueue = super.getQueue();
            if (taskQueue instanceof TaskQueue) {
                final TaskQueue queue = (TaskQueue) taskQueue;
                if (!queue.forceTaskIntoQueue(command)) {
                    submittedTaskCount.decrementAndGet();
                    throw new RejectedExecutionException("队列已满");
                }
            } else {
                submittedTaskCount.decrementAndGet();
                throw rx;
            }
        }
    }

    /**
     * Custom TaskQueue
     */
    public static class TaskQueue extends LinkedBlockingQueue<Runnable> {
        private EnhancedThreadPoolExecutor executor;

        public TaskQueue(int capacity) {
            super(capacity);
        }

        public void setExecutor(EnhancedThreadPoolExecutor exec) {
            executor = exec;
        }

        public boolean forceTaskIntoQueue(Runnable o) {
            if (executor.isShutdown()) {
                throw new RejectedExecutionException("Executor已经关闭了,不能将task添加到队列里面");
            }
            return super.offer(o);
        }

        @Override
        public boolean offer(Runnable o) {
            int currentPoolThreadSize = executor.getPoolSize();
            //如果线程池里的线程数量已经到达最大,将任务添加到队列中
            if (currentPoolThreadSize == executor.getMaximumPoolSize()) {
                return super.offer(o);
            }
            //说明有空闲的线程,这个时候无需创建core线程之外的线程,而是把任务直接丢到队列里即可
            if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
                return super.offer(o);
            }

            //如果线程池里的线程数量还没有到达最大,直接创建线程,而不是把任务丢到队列里面
            if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
                return false;
            }

            return super.offer(o);
        }
    }

    /**
     * Test
     */
    public static class TestExecutor {
        private static final int CORE_SIZE = 5;
        private static final int MAX_SIZE = 10;
        private static final long KEEP_ALIVE_TIME = 30;
        private static final int QUEUE_SIZE = 5;

        static EnhancedThreadPoolExecutor executor = new EnhancedThreadPoolExecutor(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new TaskQueue(QUEUE_SIZE));

        public static void main(String[] args) {
            for (int i = 0; i < 15; i++) {
                executor.execute(() -> {
                    try {
                        Thread.currentThread();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                System.out.println("线程池中现在的线程数目：" + executor.getPoolSize() + ",  队列中正在等待执行的任务数量：" + executor.getQueue().size());
            }
        }
    }

}
