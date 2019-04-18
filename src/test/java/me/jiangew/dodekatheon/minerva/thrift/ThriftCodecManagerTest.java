package me.jiangew.dodekatheon.minerva.thrift;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.airlift.drift.codec.ThriftCodec;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.codec.internal.coercion.DefaultJavaCoercions;
import io.airlift.drift.codec.internal.compiler.CompilerThriftCodecFactory;
import io.airlift.drift.protocol.TBinaryProtocol;
import io.airlift.drift.protocol.TMemoryBuffer;
import io.airlift.drift.protocol.TProtocol;
import io.airlift.drift.protocol.TTransport;
import me.jiangew.dodekatheon.minerva.thrift.FrozenContext;
import me.jiangew.dodekatheon.minerva.thrift.Subaccount;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author jiangew
 * @description
 */
public class ThriftCodecManagerTest {

  private ThriftCodecManager readCodecManager;
  private ThriftCodecManager writeCodecManager;

  private ThriftCodecManager manager = new ThriftCodecManager(new CompilerThriftCodecFactory(false));

  private ConsoleReporter reporter;
  private final MetricRegistry metrics = new MetricRegistry();
  private final Timer ser = metrics.timer("ser");
  private final Timer des = metrics.timer("des");

  @BeforeMethod
  protected void setUp() {
    readCodecManager = createReadCodecManager();
    writeCodecManager = createWriteCodecManager();
    readCodecManager.getCatalog().addDefaultCoercions(DefaultJavaCoercions.class);
    writeCodecManager.getCatalog().addDefaultCoercions(DefaultJavaCoercions.class);

    reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MICROSECONDS).build();
  }

  @Test
  public void freezeSubaccount() throws Exception {
    reporter.start(1, TimeUnit.SECONDS);
    for (int i = 0; i < 1000; i++) {
      testRoundTripSerialize(createSubaccount(), TBinaryProtocol::new);
    }
  }

  public Subaccount createSubaccount() {
    Map<String, FrozenContext> contexts = new HashMap<>();
    for (int i = 0; i < 2000; i++) {
      Set<String> transactions = new HashSet<>();
      transactions.add("transaction_" + new Random(100).nextInt());
      transactions.add("transaction_" + new Random(100).nextInt());
      transactions.add("transaction_" + new Random(100).nextInt());
      transactions.add("transaction_" + new Random(100).nextInt());
      transactions.add("transaction_" + new Random(100).nextInt());

      FrozenContext context = new FrozenContext("btcusdt", String.valueOf(i), "123", 10000, transactions);
      contexts.put(String.valueOf(i), context);
    }

    Subaccount subaccount = new Subaccount("123", 3838, 1);
    subaccount.setContexts(contexts);
    return subaccount;
  }

  public ThriftCodecManager createReadCodecManager() {
    return manager;
  }

  public ThriftCodecManager createWriteCodecManager() {
    return manager;
  }

  private <T> T testRoundTripSerialize(T value, Function<TTransport, TProtocol> protocolFactory) throws Exception {
    ThriftCodec<T> readCodec = (ThriftCodec<T>) readCodecManager.getCodec(value.getClass());
    ThriftCodec<T> writeCodec = (ThriftCodec<T>) writeCodecManager.getCodec(value.getClass());

    return testRoundTripSerialize(readCodec, writeCodec, value, protocolFactory);
  }

  private <T> T testRoundTripSerialize(
      ThriftCodec<T> readCodec, ThriftCodec<T> writeCodec, T structInstance, Function<TTransport, TProtocol> protocolFactory) throws Exception {
    Class<T> structClass = (Class<T>) structInstance.getClass();
    return testRoundTripSerialize(readCodec, writeCodec, structClass, structInstance, protocolFactory);
  }

  private <T> T testRoundTripSerialize(
      ThriftCodec<T> readCodec, ThriftCodec<T> writeCodec, Type structType, T structInstance,
      Function<TTransport, TProtocol> protocolFactory) throws Exception {

    // ThriftCatalog readCatalog = readCodecManager.getCatalog();
    // ThriftStructMetadata readMetadata = readCatalog.getThriftStructMetadata(structType);
    // assertNotNull(readMetadata);
    //
    // ThriftCatalog writeCatalog = writeCodecManager.getCatalog();
    // ThriftStructMetadata writeMetadata = writeCatalog.getThriftStructMetadata(structType);
    // assertNotNull(writeMetadata);

    TMemoryBuffer transport = new TMemoryBuffer(10 * 1024);
    TProtocol protocol = protocolFactory.apply(transport);

    final Timer.Context serContext = ser.time();
    try {
      writeCodec.write(structInstance, protocol);
    } finally {
      serContext.stop();
    }

    T copy = null;
    final Timer.Context desContext = des.time();
    try {
      copy = readCodec.read(protocol);
    } finally {
      desContext.stop();
    }

    // assertNotNull(copy);
    // assertEquals(copy, structInstance);

    return copy;
  }

}