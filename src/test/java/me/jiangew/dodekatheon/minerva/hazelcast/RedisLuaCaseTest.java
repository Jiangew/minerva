package me.jiangew.dodekatheon.minerva.hazelcast;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.api.RedissonClient;
import org.redisson.codec.DefaultReferenceCodecProvider;
import org.redisson.codec.ReferenceCodecProvider;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 * @author jiangew
 * @desc
 */
public class RedisLuaCaseTest {

  private static final String SUBACCOUNTS_KEY = "subaccounts";
  private static final String ORDERS_KEY = "orders";
  private static final EventLoopGroup SHARED_EVENT_LOOP_GROUP;

  private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
  private ConsoleReporter reporter;
  private final MetricRegistry metrics = new MetricRegistry();
  private final Timer logic = metrics.timer("logic");

  static {
    EventLoopGroup eventLoopGroup;
    try {
      eventLoopGroup = new EpollEventLoopGroup();
    } catch (Throwable e) {
      eventLoopGroup = new NioEventLoopGroup();
    }
    SHARED_EVENT_LOOP_GROUP = eventLoopGroup;
  }

  private static final String addScript = "local amount = tonumber(ARGV[1])\n"
      + "redis.call('HINCRBYFLOAT',\"subaccounts\",KEYS[1],amount)\n"
      + "redis.call('HINCRBYFLOAT',\"orders\",KEYS[1],amount)";

  private static String addSHA = "";

  private RedissonClient defaultRedissonClient;

  private ReferenceCodecProvider codecProvider = new DefaultReferenceCodecProvider();

  @Before
  public void setUp() throws Exception {
    Config config = new Config();
    config.setEventLoopGroup(SHARED_EVENT_LOOP_GROUP);
    config.setUseLinuxNativeEpoll(SHARED_EVENT_LOOP_GROUP instanceof EpollEventLoopGroup);

    //config.setCodec(StringCodec.INSTANCE);
    config.setReferenceCodecProvider(codecProvider);

    SingleServerConfig serverConfig = config.useSingleServer();
    serverConfig.setAddress("redis://172.18.1.151:6379");
    serverConfig.setDatabase(11);
    // serverConfig.setAddress("redis://127.0.0.1:6379");
    // serverConfig.setDatabase(0);

    defaultRedissonClient = Redisson.create(config);
    addSHA = defaultRedissonClient.getScript().scriptLoad(addScript);

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

    final List<Integer> plus = IntStream.range(1, 1 + count).mapToObj(Integer::new).collect(Collectors.toList());
    AtomicInteger subaccountIdGenerator = new AtomicInteger(1);

    final List<Thread> threads = plus.stream().map(amount -> getThread(latch, amount, subaccountIdGenerator.getAndIncrement()))
        .collect(Collectors.toList());

    // final List<Thread> threads = plus.stream().map(amount -> {
    //     Integer orderId = subaccountIdGenerator.incrementAndGet();
    //     return getThread(orderId, latch, orderId);
    // }).collect(Collectors.toList());
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

  private Thread getThread(CountDownLatch latch, Integer amount, Integer orderId) {
    return new Thread(() -> {
      int times = 100;
      while (times-- > 0) {
        final Timer.Context context = logic.time();
        try {
          frozen(orderId, amount);
          latch.countDown();
        } finally {
          context.stop();
        }
      }

      // change(transactionId, amount);
      // System.out.println(Thread.currentThread().getName() + ": start is "+start+" :" + (System.currentTimeMillis() - start)
      // +" id is "+transactionId+"  : amount is "+amount);
      // latch.countDown();
    });
  }

  private void frozen(Integer orderId, Integer amount) {
    List<Object> keyList = new ArrayList<>();
    keyList.add(orderId);
    defaultRedissonClient.getScript().evalSha(Mode.READ_WRITE, addSHA, ReturnType.INTEGER, keyList, amount);

    // RMap<Integer, Integer> subaccounts = defaultRedissonClient.getMap(SUBACCOUNTS_KEY);
    // subaccounts.putIfAbsent(orderId, 0);
    // RLock subaccountsLock = subaccounts.getLock(orderId);
    //
    // subaccountsLock.lock(5, TimeUnit.SECONDS);
    // RMap<Integer, Integer> orders = defaultRedissonClient.getMap(ORDERS_KEY);
    // RLock ordersLock = orders.getLock(orderId);
    // ordersLock.lock(5, TimeUnit.SECONDS);
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

  @After
  public void tearDown() throws Exception {
  }

}
