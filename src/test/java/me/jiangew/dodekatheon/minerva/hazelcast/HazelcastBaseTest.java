package me.jiangew.dodekatheon.minerva.hazelcast;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import me.jiangew.dodekatheon.minerva.hazelcast.protobuf.FreezeProtoSerializer;
import me.jiangew.dodekatheon.minerva.hazelcast.protobuf.FreezeProtos;
import me.jiangew.dodekatheon.minerva.hazelcast.protobuf.SubaccountProtoSerializer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author jiangew
 * @desc
 */
public abstract class HazelcastBaseTest {

  protected HazelcastInstance instance;
  protected String subAccountId = "0";

  protected final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
  protected ConsoleReporter reporter;
  protected final MetricRegistry metrics = new MetricRegistry();

  private final Timer logic = metrics.timer("logic");

  @Before
  public void setUp() throws Exception {
    subAccountId = RandomStringUtils.randomNumeric(10);

//        instance = HazelcastInstanceFactory.getOrCreateHazelcastInstance(new ClasspathXmlConfig("hazelcast-client.xml"));
    final ClientConfig config = new XmlClientConfigBuilder().build();
    instance = HazelcastClient.newHazelcastClient(config);

    reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();

    // final ClassDefinition classDefinition = new ClassDefinitionBuilder(1, Subaccount.CLASS_ID)
    //         .addUTFField("subAccountId")
    //         .addUTFField("availableBalance")
    //         .addPortableArrayField("freeze", new ClassDefinitionBuilder(1, Freeze.CLASS_ID)
    //                 .addUTFField("orderId")
    //                 .addUTFField("amount")
    //                 .build()).build();
    //
    // config.getSerializationConfig().addClassDefinition(classDefinition);

    SerializerConfig subaccountSerializerConfig = new SerializerConfig()
        .setTypeClass(FreezeProtos.Subaccount.class)
        .setImplementation(new SubaccountProtoSerializer());

    SerializerConfig freezeSerializerConfig = new SerializerConfig()
        .setTypeClass(FreezeProtos.Freeze.class)
        .setImplementation(new FreezeProtoSerializer());

    config.getSerializationConfig()
        .addSerializerConfig(subaccountSerializerConfig)
        .addSerializerConfig(freezeSerializerConfig);
  }

  @After
  public void tearDown() throws Exception {
    instance.shutdown();
  }

  public void coccurrently(List<BigDecimal> amounts) throws InterruptedException {
    AtomicInteger transactionId = new AtomicInteger(1);

    final List<Thread> threads = amounts.stream().map(amount -> getThread(amount, transactionId.getAndIncrement())).collect(Collectors.toList());

//        threads.addAll(plus.stream().map(BigDecimal::negate).map(amount -> getThread(latch, amount, transactionId.getAndIncrement())).collect(Collectors.toList()));
//
//        // transaction id duplicate
//        transactionId.set(1);
//
//        threads.addAll(plus.stream().map(amount -> getThread(latch, amount, transactionId.getAndIncrement())).collect(Collectors.toList()));
//        threads.addAll(plus.stream().map(BigDecimal::negate).map(amount -> getThread(latch, amount, transactionId.getAndIncrement())).collect(Collectors.toList()));

    reporter.start(1, TimeUnit.SECONDS);

    Collections.shuffle(threads);

    for (Thread thread : threads) executor.submit(thread);

    while (executor.getActiveCount() > 0) {
      executor.awaitTermination(2, TimeUnit.SECONDS);
    }

    wait2Seconds();


//        final Collection<Object> transactions = instance.getMultiMap(ORDERS_MAP_KEY).get(subAccountId);
//        Assert.assertEquals(2 * count, transactions.size());
//
//        final IMap<String, BigDecimal> orders = instance.getMap(SUBACCOUNT_MAP_KEY);
//        Assert.assertEquals(0, orders.get(subAccountId).intValue());
  }

  static void wait2Seconds() {
    try {
      Thread.sleep(2 * 1000);
    } catch (InterruptedException e) {
    }
  }

  private Thread getThread(BigDecimal amount, int orderId) {
    return new Thread(() -> {
      int times = 100;
      while (times-- > 0) {
//                final long start = System.currentTimeMillis();
        final Timer.Context context = logic.time();
        try {
          freeze(subAccountId, UUID.randomUUID().toString(), amount);
//                    System.out.println(Thread.currentThread().getName() + ":" + (System.currentTimeMillis() - start));
        } catch (Throwable t) {
          t.printStackTrace();
        } finally {
          context.stop();
        }
      }
    });
  }

  protected abstract void freeze(String subAccountId, String orderId, BigDecimal amount);

}
