package me.jiangew.dodekatheon.minerva.hazelcast;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.codec.DefaultReferenceCodecProvider;
import org.redisson.codec.ReferenceCodecProvider;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiangew
 * @desc
 */
public class RedissonTxLockCaseTest {

  private static final String SUBACCOUNTS_KEY = "subaccounts";
  private static final String ORDERS_KEY = "orders";
  private static final EventLoopGroup SHARED_EVENT_LOOP_GROUP;

  private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
  private ConsoleReporter reporter;
  private final MetricRegistry metrics = new MetricRegistry();
  private final Timer tx = metrics.timer("transaction");
  private final Timer orderLock = metrics.timer("orderLock");
  private final Timer orderUnlock = metrics.timer("orderUnlock");
  private final Timer accountLock = metrics.timer("accountLock");
  private final Timer accountUnlock = metrics.timer("accountUnlock");
  private final Timer orderGet = metrics.timer("orderGet");
  private final Timer orderPut = metrics.timer("orderPut");
  private final Timer accountGet = metrics.timer("accountGet");
  private final Timer accountPut = metrics.timer("accountPut");

  static {
    EventLoopGroup eventLoopGroup;
    try {
      eventLoopGroup = new EpollEventLoopGroup();
    } catch (Throwable e) {
      eventLoopGroup = new NioEventLoopGroup();
    }
    SHARED_EVENT_LOOP_GROUP = eventLoopGroup;
  }

  private RedissonClient redisson;

  private ReferenceCodecProvider codecProvider = new DefaultReferenceCodecProvider();

  @Before
  public void setUp() throws Exception {
    Config config = new Config();
    config.setEventLoopGroup(SHARED_EVENT_LOOP_GROUP);
    config.setUseLinuxNativeEpoll(SHARED_EVENT_LOOP_GROUP instanceof EpollEventLoopGroup);

    //config.setCodec(StringCodec.INSTANCE);
    config.setReferenceCodecProvider(codecProvider);

    SingleServerConfig serverConfig = config.useSingleServer();
    serverConfig.setAddress("redis://127.0.0.1:6379");
    serverConfig.setDatabase(11);
    // serverConfig.setAddress("redis://127.0.0.1:6379");
    // serverConfig.setDatabase(0);

    redisson = Redisson.create(config);

    reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
  }

  @Test
  public void concurrently() throws InterruptedException {
    final int count = 10;
    final int times = 1;

    final CountDownLatch latch = new CountDownLatch(times * count);

    final List<BigDecimal> plus = IntStream.range(1, 1 + count).mapToObj(BigDecimal::new).collect(Collectors.toList());
    AtomicInteger subaccountIdGenerator = new AtomicInteger(1);

    final List<Thread> threads = plus.stream().map(amount -> getThread(latch, amount, subaccountIdGenerator.getAndIncrement()))
        .collect(Collectors.toList());

    // final List<Thread> threads = plus.stream().map(amount -> {
    //     Integer orderId = subaccountIdGenerator.incrementAndGet();
    //     return getThread(orderId, latch, orderId);
    // }).collect(Collectors.toList());
    //
    // threads.addAll(plus.stream().map(amount -> {
    //     Integer orderId = subaccountIdGenerator.incrementAndGet();
    //     return getThread(orderId, latch, -orderId);
    // }).collect(Collectors.toList()));
    //
    // // transaction id duplicate
    // subaccountIdGenerator.set(0);
    //
    // threads.addAll(plus.stream().map(amount -> {
    //     Integer orderId = subaccountIdGenerator.incrementAndGet();
    //     return getThread(orderId, latch, -orderId);
    // }).collect(Collectors.toList()));
    //
    // threads.addAll(plus.stream().map(amount -> {
    //     Integer orderId = subaccountIdGenerator.incrementAndGet();
    //     return getThread(orderId, latch, orderId);
    // }).collect(Collectors.toList()));

    reporter.start(1, TimeUnit.SECONDS);

    Collections.shuffle(threads);

    for (Thread thread : threads) executor.submit(thread);

    latch.await();

    wait2Seconds();

    // final RMap<Integer, Integer> orders = defaultRedissonClient.getMap(ORDERS_KEY);
    // orders.forEach((orderId,amount)->{
    //     System.out.println("finally orderId : " + orderId+" ,amount : "+amount);
    // });
    // final RMap<Integer, Integer> subaccounts = defaultRedissonClient.getMap(SUBACCOUNTS_KEY);
    // subaccounts.forEach((subaccountId,balance)->{
    //     System.out.println("finally subaccountId : " + subaccountId+" ,amount : "+balance);
    // });
  }

  static void wait2Seconds() {
    try {
      Thread.sleep(2 * 1000);
    } catch (InterruptedException e) {
    }
  }

  private Thread getThread(CountDownLatch latch, BigDecimal amount, Integer orderId) {
    return new Thread(() -> {
      int times = 100;
      while (times-- > 0) {
        final Timer.Context context = tx.time();
        try {
          frozenNew(21766, orderId * 1000 + times, amount);
          latch.countDown();
        } finally {
          context.stop();
        }
      }
    });
  }

  private void frozen(Integer accountId, Integer orderId, BigDecimal amount) {
    RTransaction transaction = redisson.createTransaction(TransactionOptions.defaults()
        .syncSlavesTimeout(5, TimeUnit.SECONDS)
        .responseTimeout(3, TimeUnit.SECONDS)
        .retryInterval(2, TimeUnit.SECONDS)
        .retryAttempts(3)
        .timeout(10, TimeUnit.SECONDS));
    RMap<Integer, BigDecimal> orders = transaction.getMap(ORDERS_KEY);
    RLock ordersLock = null;
    RLock subaccountsLock = null;
    final Timer.Context orderLockContext = orderLock.time();
    try {
      ordersLock = redisson.getLock(orderId.toString());
      ordersLock.lock();
    } finally {
      orderLockContext.stop();
    }

    try {
      final Timer.Context orderGetContext = orderGet.time();
      BigDecimal order;
      try {
        order = orders.get(orderId);
      } finally {
        orderGetContext.stop();
      }

      if (order == null) {
        final Timer.Context orderPutContext = orderPut.time();
        try {
          orders.put(orderId, amount);
        } finally {
          orderPutContext.stop();
        }

        RMap<Integer, BigDecimal> subaccounts = transaction.getMap(SUBACCOUNTS_KEY);
        final Timer.Context accountLockContext = accountLock.time();
        try {
          subaccountsLock = redisson.getLock(accountId.toString());
          subaccountsLock.lock();
        } finally {
          accountLockContext.stop();
        }

        final Timer.Context accountGetContext = accountGet.time();
        BigDecimal subaccount;
        try {
          subaccount = subaccounts.get(accountId);
        } finally {
          accountGetContext.stop();
        }
        final Timer.Context accountPutContext = accountPut.time();
        try {
          subaccounts.put(accountId, subaccount.subtract(amount));
        } finally {
          accountPutContext.stop();
        }

        transaction.commit();
      }
    } catch (Exception e) {
      transaction.rollback();
    } finally {
      final Timer.Context orderUnlockContext = orderUnlock.time();
      try {
        ordersLock.unlock();
      } finally {
        orderUnlockContext.stop();
      }
      final Timer.Context accountUnlockContext = accountUnlock.time();
      try {
        subaccountsLock.unlock();
      } finally {
        accountUnlockContext.stop();
      }
    }

    // RMap<Integer, BigDecimal> subaccounts = defaultRedissonClient.getMap(SUBACCOUNTS_KEY);
    // subaccounts.putIfAbsent(accountId, BigDecimal.ZERO);
    // RLock subaccountsLock = subaccounts.getLock(accountId);
    // subaccountsLock.lock(5, TimeUnit.SECONDS);
    // try {
    //     orders.putIfAbsent(orderId, 0);
    //     Integer oldAmount = orders.get(orderId);
    //     orders.put(orderId, oldAmount + amount);
    //     Integer oldBalance = subaccounts.get(orderId);
    //     subaccounts.put(orderId, oldBalance + amount);
    // } finally {
    //     ordersLock.unlock();
    //     subaccountsLock.unlock();
    // }
  }

  private void frozenNew(Integer accountId, Integer orderId, BigDecimal amount) {
    RTransaction transaction = redisson.createTransaction(TransactionOptions.defaults()
        .syncSlavesTimeout(5, TimeUnit.SECONDS)
        .responseTimeout(3, TimeUnit.SECONDS)
        .retryInterval(2, TimeUnit.SECONDS)
        .retryAttempts(3)
        .timeout(10, TimeUnit.SECONDS));
    RMap<Integer, BigDecimal> subaccounts = transaction.getMap(SUBACCOUNTS_KEY);
    RLock subaccountsLock = null;
    final Timer.Context accountLockContext = accountLock.time();
    try {
      subaccountsLock = redisson.getLock(accountId.toString());
      subaccountsLock.lock();
    } finally {
      accountLockContext.stop();
    }

    try {
      RMap<Integer, BigDecimal> orders = transaction.getMap(ORDERS_KEY);
      final Timer.Context orderGetContext = orderGet.time();
      BigDecimal order;
      try {
        order = orders.get(orderId);
      } finally {
        orderGetContext.stop();
      }

      if (order == null) {
        final Timer.Context orderPutContext = orderPut.time();
        try {
          orders.put(orderId, amount);
        } finally {
          orderPutContext.stop();
        }

        final Timer.Context accountGetContext = accountGet.time();
        BigDecimal subaccount;
        try {
          subaccount = subaccounts.get(accountId);
        } finally {
          accountGetContext.stop();
        }
        final Timer.Context accountPutContext = accountPut.time();
        try {
          subaccounts.put(accountId, subaccount.subtract(amount));
        } finally {
          accountPutContext.stop();
        }

        transaction.commit();
      }
    } catch (Exception e) {
      transaction.rollback();
    } finally {
      final Timer.Context accountUnlockContext = accountUnlock.time();
      try {
        subaccountsLock.unlock();
      } finally {
        accountUnlockContext.stop();
      }
    }
  }

  @After
  public void tearDown() throws Exception {
  }

}
