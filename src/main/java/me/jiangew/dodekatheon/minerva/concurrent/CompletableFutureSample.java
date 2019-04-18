package me.jiangew.dodekatheon.minerva.concurrent;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.junit.Assert.*;

/**
 * Desc: 20 Examples of Using Java’s CompletableFuture
 * Blog: https://dzone.com/articles/what-is-project-amber-in-java-1
 * <p>
 * Author: Jiangew
 * Date: 17/12/2017
 */
public class CompletableFutureSample {

    static ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
        int count = 1;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "custom-executor-" + count++);
        }
    });

    static Random random = new Random();

    public static void main(String[] args) {
        try {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.complete("Well");
            future.thenAccept(System.out::println).thenAccept(v -> System.out.println("done"));
        } finally {
            executor.shutdown();
        }

    }

    /**
     * Creating a Completed CompletableFuture
     */
    static void completedFutureExample() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
        assertTrue(cf.isDone());
        assertEquals("message", cf.getNow(null));
    }

    /**
     * Running a Simple Asynchronous Stage
     */
    static void runAsyncExample() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            assertTrue(Thread.currentThread().isDaemon());
            // randomSleep();
        });
        assertFalse(cf.isDone());
        // sleepEnough();
        assertTrue(cf.isDone());
    }

    /**
     * Applying a Function on Previous Stage
     */
    static void thenApplyExample() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApply(s -> {
            assertFalse(Thread.currentThread().isDaemon());
            return s.toUpperCase();
        });
        assertEquals("MESSAGE", cf.getNow(null));
    }

    /**
     * Asynchronously Applying a Function on Previous Stage
     */
    static void thenApplyAsyncExample() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().isDaemon());
            // randomSleep();
            return s.toUpperCase();
        });
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    /**
     * Asynchronously Applying a Function on Previous Stage Using a Custom Executor
     */
    static void thenApplyAsyncWithExecutorExample() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().getName().startsWith("custom-executor-"));
            assertFalse(Thread.currentThread().isDaemon());
            // randomSleep();
            return s.toUpperCase();
        }, executor);

        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    /**
     * Consuming Result of Previous Stage
     */
    static void thenAcceptExample() {
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message").thenAccept(s -> result.append(s));
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * Asynchronously Consuming Result of Previous Stage
     */
    static void thenAcceptAsyncExample() {
        StringBuilder result = new StringBuilder();
        CompletableFuture<Void> cf = CompletableFuture.completedFuture("thenAcceptAsync message").thenAcceptAsync(s -> result.append(s));

        cf.join();
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * Completing a Computation Exceptionally
     */
    static void completeExceptionallyExample() {
//        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(String::toUpperCase, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
//        CompletableFuture<String> exceptionHandler = cf.handle((s, th) -> {return (th != null) ? "message upon cancel" : "";});
//        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
//        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
//
//        try {
//            cf.join();
//            fail("Should have thrown an exception");
//        } catch (CompletionException ex) {
//            assertEquals("completed exceptionally", ex.getCause().getMessage());
//        }
//
//        assertEquals("message upon cancel", exceptionHandler.join());
    }

    /**
     * Canceling a Computation
     */
    static void cancelExample() {
        //        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(String::toUpperCase, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
        //        CompletableFuture<String> cf2 = cf.exceptionally(throwable -> "canceled message");
        //        assertTrue("Was not canceled", cf.cancel(true));
        //        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        //        assertEquals("canceled message", cf2.join());
    }

    /**
     * Applying a Function to Result of Either of Two Completed Stages
     */
    static void applyToEitherExample() {
        //        String original = "Message";
        //        CompletableFuture<String> cf1 = CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedUpperCase(s));
        //        CompletableFuture<String> cf2 = cf1.applyToEither(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)), s -> s + " from applyToEither");
        //        assertTrue(cf2.join().endsWith(" from applyToEither"));
    }

    /**
     * Consuming Result of Either of Two Completed Stages
     */
    static void acceptEitherExample() {
        //        String original = "Message";
        //        StringBuilder result = new StringBuilder();
        //        CompletableFuture<Void> cf = CompletableFuture.completedFuture(original)
        //                                                      .thenApplyAsync(s -> delayedUpperCase(s))
        //                                                      .acceptEither(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
        //                                                              s -> result.append(s).append("acceptEither"));
        //        cf.join();
        //        assertTrue("Result was empty", result.toString().endsWith("acceptEither"));
    }

    /**
     * Running a Runnable upon Completion of Both Stages
     */
    static void runAfterBothExample() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original)
                         .thenApply(String::toUpperCase)
                         .runAfterBoth(CompletableFuture.completedFuture(original).thenApply(String::toLowerCase), () -> result.append("done"));
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * Accepting Results of Both Stages in a Biconsumer
     */
    static void thenAcceptBothExample() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original)
                         .thenApply(String::toUpperCase)
                         .thenAcceptBoth(CompletableFuture.completedFuture(original).thenApply(String::toLowerCase), (s1, s2) -> result.append(s1 + s2));
        assertEquals("MESSAGEmessage", result.toString());
    }

    /**
     * Applying a Bifunction on Results of Both Stages (i.e. Combining Their Results)
     */
    static void thenCombineExample() {
        //        String original = "Message";
        //        CompletableFuture<String> cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s))
        //                                                        .thenCombine(CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s)),
        //                                                                (s1, s2) -> s1 + s2);
        //        assertEquals("MESSAGEmessage", cf.getNow(null));
    }

    /**
     * Asynchronously Applying a BiFunction on (i.e. Combining) Results of Both Stages
     */
    static void thenCombineAsyncExample() {
        //        String original = "Message";
        //        CompletableFuture<String> cf = CompletableFuture.completedFuture(original)
        //                                                        .thenApplyAsync(s -> delayedUpperCase(s))
        //                                                        .thenCombine(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
        //                                                                (s1, s2) -> s1 + s2);
        //        assertEquals("MESSAGEmessage", cf.join());
    }

    /**
     * Composing CompletableFutures
     */
    static void thenComposeExample() {
        //        String original = "Message";
        //        CompletableFuture<String> cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s))
        //                                                        .thenCompose(upper -> CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s))
        //                                                                                               .thenApply(s -> upper + s));
        //        assertEquals("MESSAGEmessage", cf.join());
    }

    /**
     * Creating a Stage That Completes When Any of Several Stages Completes
     */
    static void anyOfExample() {
        //        StringBuilder result = new StringBuilder();
        //        List messages = Arrays.asList("a", "b", "c");
        //        List<CompletableFuture<String>> futures = messages.stream()
        //                                                          .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
        //                                                          .collect(Collectors.toList());
        //        CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()])).whenComplete((res, th) -> {
        //            if(th == null) {
        //                assertTrue(isUpperCase((String) res));
        //                result.append(res);
        //            }
        //        });
        //        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * Creating a Stage That Completes When All Stages Complete
     */
    static void allOfExample() {
        //        StringBuilder result = new StringBuilder();
        //        List messages = Arrays.asList("a", "b", "c");
        //        List<CompletableFuture<String>> futures = messages.stream()
        //                                                          .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
        //                                                          .collect(Collectors.toList());
        //        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).whenComplete((v, th) -> {
        //            futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
        //            result.append("done");
        //        });
        //        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * Creating a Stage That Completes Asynchronously When All Stages Complete
     */
    static void allOfAsyncExample() {
        //        StringBuilder result = new StringBuilder();
        //        List messages = Arrays.asList("a", "b", "c");
        //        List<CompletableFuture<String>> futures = messages.stream()
        //                                                          .map(msg -> CompletableFuture.completedFuture(msg).thenApplyAsync(s -> delayedUpperCase(s)))
        //                                                          .collect(Collectors.toList());
        //        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
        //                                                         .whenComplete((v, th) -> {
        //                                                             futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
        //                                                             result.append("done");
        //                                                         });
        //        allOf.join();
        //        assertTrue("Result was empty", result.length() > 0);
    }

    private static boolean isUpperCase(String s) {
        for(int i = 0; i < s.length(); i++) {
            if(Character.isLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String delayedUpperCase(String s) {
        randomSleep();
        return s.toUpperCase();
    }

    private static String delayedLowerCase(String s) {
        randomSleep();
        return s.toLowerCase();
    }

    private static void randomSleep() {
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            // ...
        }
    }

    private static void sleepEnough() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ...
        }
    }

}