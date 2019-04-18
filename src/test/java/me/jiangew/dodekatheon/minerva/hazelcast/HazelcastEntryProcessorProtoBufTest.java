package me.jiangew.dodekatheon.minerva.hazelcast;

import com.hazelcast.core.IMap;
import me.jiangew.dodekatheon.minerva.hazelcast.portable.SubaccountEntryProcessor;
import me.jiangew.dodekatheon.minerva.hazelcast.protobuf.FreezeProtos;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiangew
 * @desc
 */
public class HazelcastEntryProcessorProtoBufTest extends HazelcastBaseTest {

  private IMap<String, FreezeProtos.Subaccount> subAccounts;
  private final Double totalAvailable = Double.valueOf(100000000);

  @Before
  public void setUp() throws Exception {
    super.setUp();

    subAccounts = instance.getMap("subaccount");
    FreezeProtos.Subaccount subaccount = FreezeProtos.Subaccount.newBuilder().setSubAccountId(subAccountId).setAvailableBalance(totalAvailable)
        .setVersion("1").build();
    subAccounts.put(subAccountId, subaccount);
  }

  @Test
  public void subAccountEntryProcessor() throws Exception {
    System.out.println("original balance of " + subAccountId + "= " + subAccounts.get(subAccountId).getAvailableBalance());
    final int expected = (int) (totalAvailable + Double.valueOf(5050 * 100));
    System.out.println("expected balance = " + expected);
    final IntStream range = IntStream.range(1, 10 + 1);

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        System.out.println("current available balance:" + subAccounts.get(subAccountId).getAvailableBalance());
      }
    }, 1000, 2000);

    // test logic
    coccurrently(range.mapToObj(BigDecimal::new).collect(Collectors.toList()));

    Assert.assertEquals(0, subAccounts.get(subAccountId).getAvailableBalance() + (Double.valueOf(expected)));
    Assert.assertEquals(100 * 100, subAccounts.get(subAccountId).getFreeze().size());
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  protected void freeze(String subAccountId, String orderId, BigDecimal amount) {
    final Object o = subAccounts.executeOnKey(subAccountId, new SubaccountEntryProcessor(orderId, subAccountId, amount));
    Assert.assertTrue(o != null);
  }

}
