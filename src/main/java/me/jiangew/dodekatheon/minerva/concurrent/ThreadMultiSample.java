package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.concurrent.Callable;

/**
 * Author: Jiangew
 * Date: 20/02/2017
 * <p>
 * Java 中实现多线程的3种方法
 * 1 继承Thread 类
 * 2 实现Runnable接口
 * 3 实现Callable接口
 * <p>
 * 实现Runnable接口 VS 继承Thread类：
 * 1.可以避免由于Java的单继承特性而带来的局限；
 * 2.增强程序的健壮性，代码能够被多个线程共享，代码与数据是独立的；
 * 3.适合多个相同程序代码的线程区处理同一资源的情况
 * <p>
 * 实现Runnable接口 VS 实现Callable接口:
 * 1.Runnable是自从java1.1就有了，而Callable是1.5之后才加上去的
 * 2.Callable规定的方法是call()，Runnable规定的方法是run()
 * 3.Callable的任务执行后可返回值，而Runnable的任务是不能返回值(void)
 * 4.call方法可以抛出异常，run方法不可以
 * 5.运行Callable任务可以拿到一个Future对象，表示异步计算的结果。它提供了检查计算是否完成的方法，以等待计算的完成，并检索计算的结果。通过Future对象可以了解任务执行情况，可取消任务的执行，还可获取执行结果；
 * 6.加入线程池运行，Runnable使用ExecutorService的execute方法，Callable使用submit方法。
 */
public class ThreadMultiSample {

    public static void main(String[] args) {

        // method 1：继承Thread
        new ThreadExtends().start();
        new ThreadExtends().start();
        new ThreadExtends().start();

        // method 2：实现Runnable接口
//        ThreadRunnable runnable = new ThreadRunnable();
//        new Thread(runnable).start();
//        new Thread(runnable).start();

        // method 3：实现Callable接口
//        ThreadCallable callable = new ThreadCallable();
//        FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
//        new Thread(futureTask, "线程名：有返回值的线程2").start();
//
//        try {
//            System.out.println("子线程的返回值：" + futureTask.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    /**
     * java类的单一继承特性问题
     */
    public static class ThreadExtends extends Thread {
        private int ticket = 5;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                if (ticket > 0) {
                    ticket--;
                    System.out.println("车票第" + ticket + "张");
                }
            }
        }
    }

    /**
     * Runnable是可以共享数据的，多个Thread可以同时加载一个Runnable，当各自Thread获得CPU时间片的时候开始运行Runnable，Runnable里面的资源是被共享的，所以使用Runnable更加的灵活。
     */
    public static class ThreadRunnable implements Runnable {
        private int ticket = 5;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                if (ticket > 0) {
                    ticket--;
                    System.out.println("车票第" + ticket + "张");
                }
            }
        }
    }

    /**
     * Runnable是执行工作的独立任务，但是它不返回任何值。如果你希望任务在完成的能返回一个值，那么可以实现Callable接口而不是Runnable接口。
     * 在Java SE5中引入的Callable是一种具有类型参数的泛型，它的参数类型表示的是从方法call()(不是run())中返回的值。
     */
    public static class ThreadCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程名：" + Thread.currentThread().getName());
            int i = 0;
            for (; i < 5; i++) {
                System.out.println("变量i：" + i);
            }

            return i;
        }
    }
}
