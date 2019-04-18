package me.jiangew.dodekatheon.minerva.hazelcast.protobuf;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangew
 * @desc
 */
public class FreezeProtosTest {
  private ConsoleReporter reporter;
  private Timer serialize;
  private Timer unSerialize;
  private MetricRegistry metrics;

  private final static long KB_FACTOR = 1024;
  private final static long MB_FACTOR = 1024 * KB_FACTOR;
  private final static long GB_FACTOR = 1024 * MB_FACTOR;

  public static double parse(String arg0) {
    int spaceNdx = arg0.indexOf(" ");
    double ret = Double.parseDouble(arg0.substring(0, spaceNdx));
    switch (arg0.substring(spaceNdx + 1)) {
      case "GB":
        return ret * GB_FACTOR;
      case "MB":
        return ret * MB_FACTOR;
      case "KB":
        return ret * KB_FACTOR;
    }
    return -1;
  }

  @Test
  public void name() throws IOException {

    metrics = new MetricRegistry();
    serialize = metrics.timer("serialize");
    unSerialize = metrics.timer("un-serialize");

    reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MICROSECONDS)
        .build();

    reporter.start(2, TimeUnit.SECONDS);

    int count = 2000;
    int time = 1000;
    while (time-- > 0) {
      final FreezeProtos.Subaccount.Builder builder = FreezeProtos.Subaccount.newBuilder();
      builder.setSubAccountId("subAccountId");
      builder.setAvailableBalance(198888.9302);
      builder.setVersion("version");
      while (count-- > 0) {
        String orderId = UUID.randomUUID().toString();
        builder.putFreeze(orderId, FreezeProtos.Freeze.newBuilder().setOrderId(orderId).setAmount(11000.0).build());
      }

      byte[] data = new byte[0];


      FreezeProtos.Subaccount subaccount = builder.build();
      final Timer.Context serializeContext = serialize.time();
//            ByteOutputStream outputStream = new ByteOutputStream(1024*1034);

      try {
        subaccount.toByteArray();
//                subaccount.writeTo(outputStream);
      } finally {
        serializeContext.stop();
      }

      final Timer.Context deserializeContext = unSerialize.time();
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.getBytes());
      try {
        final FreezeProtos.Subaccount de = FreezeProtos.Subaccount.parseFrom(data);
//                final FreezeProtos.Subaccount from = FreezeProtos.Subaccount.parseFrom(inputStream);
      } finally {
        deserializeContext.stop();
      }
    }
    wait3Seconds();
  }

  static void wait3Seconds() {
    try {
      Thread.sleep(3 * 1000);
    } catch (InterruptedException e) {
    }
  }
}