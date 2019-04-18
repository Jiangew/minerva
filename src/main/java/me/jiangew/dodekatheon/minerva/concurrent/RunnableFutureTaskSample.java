package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.concurrent.*;

/**
 * Author: Jiangew
 * Date: 20/02/2017
 * <p>
 * Runnable：
 * 其中Runnable应该是我们最熟悉的接口，它只有一个run()函数，用于将耗时操作写在其中，该函数没有返回值。
 * 然后使用某个线程去执行该runnable即可实现多线程，Thread类在调用start()函数后就是执行的是Runnable的run()函数。
 * <p>
 * Callable：
 * Callable与Runnable的功能大致相似，Callable中有一个call()函数，但是call()函数有返回值，而Runnable的run()函数不能将结果返回给客户程序。
 * <p>
 * Future：
 * Executor就是Runnable和Callable的调度容器，Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果、设置结果操作。get方法会阻塞，直到任务返回结果。
 * <p>
 * FutureTask：
 * 1.FutureTask则是一个RunnableFuture<V>，而RunnableFuture实现了Runnable又实现了Future<V>这两个接口
 * 2.FutureTask可以包装Runnable和Callable<V>， 由构造函数注入依赖
 * 3.构造函数注入时，Runnable注入会被Executors.callable()函数转换为Callable类型，即FutureTask最终都是执行Callable类型的任务
 * 4.RunnableAdapter适配器，把Runnable转换为Callable
 * 5.由于FutureTask实现了Runnable，因此它既可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行。
 * 并且还可以直接通过get()函数获取执行结果，该函数会阻塞，直到结果返回。因此FutureTask既是Future、Runnable，又是包装了Callable(如果是Runnable最终也会被转换为Callable )， 它是这两者的合体。
 */
public class RunnableFutureTaskSample {

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        runnableDemo();
        futureDemo();
    }

    /**
     * Runnable 无返回值
     */
    private static void runnableDemo() {
        new Thread(() -> System.out.println("Runnable demo: " + fibc(20))).start();
    }

    private static void futureDemo() {
        try {
            // 提交Runnable则无返回值，Future中没数据
            Future<?> result = executor.submit((Runnable) () -> fibc(20));
            System.out.println("Future result from Runnable: " + result.get());

            // 提交Callable有返回值，Future中能够获取返回值
            Future<Integer> result2 = executor.submit(() -> fibc(20));
            System.out.println("Future result from Callable: " + result2.get());

            /*
             * FutureTask则是一个RunnableFuture<V>，实现了 Runnable 和 Future<V> 2个接口，
             * 另外它还可以包装Runnable(实际上会转换为Callable)和Callable<V>，所以一般来讲是一个符合体了，
             * 它可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行，
             * 并且可以通过 v get() 返回执行结果，在线程体没有执行完成的时候，主线程一直阻塞等待，执行完则直接返回结果。
             */
            FutureTask<Integer> futureTask = new FutureTask<>(() -> fibc(20));
            executor.submit(futureTask);
            System.out.println("Future result from FutureTask: " + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 效率低下的斐波那契数列，耗时操作
     *
     * @param num
     * @return
     */
    static int fibc(int num) {
        if (num == 0) {
            return 0;
        }
        if (num == 1) {
            return 1;
        }
        return fibc(num - 1) + fibc(num - 2);
    }

}
