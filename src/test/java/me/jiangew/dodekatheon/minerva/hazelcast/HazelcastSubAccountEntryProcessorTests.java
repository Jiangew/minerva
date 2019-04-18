package me.jiangew.dodekatheon.minerva.hazelcast;

import com.hazelcast.core.IMap;
import me.jiangew.dodekatheon.minerva.hazelcast.portable.Subaccount;
import me.jiangew.dodekatheon.minerva.hazelcast.portable.SubaccountEntryProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiangew
 * @desc
 */
public class HazelcastSubAccountEntryProcessorTests extends HazelcastBaseTest {

  private IMap<String, Subaccount> subAccounts;
  private final BigDecimal totalAvailable = BigDecimal.valueOf(100000000);

  @Before
  public void setUp() throws Exception {
    super.setUp();

    subAccounts = instance.getMap("subaccount");
    subAccounts.put(subAccountId, new Subaccount(subAccountId, totalAvailable, BigInteger.ONE));
  }

  @Test
  public void subAccountEntryProcessor() throws Exception {
    System.out.println("original balance of " + subAccountId + "= " + subAccounts.get(subAccountId).getAvailableBalance().toPlainString());
    final int expected = totalAvailable.subtract(BigDecimal.valueOf(5050 * 100)).intValue();
    System.out.println("expected balance = " + expected);
    final IntStream range = IntStream.range(1, 100 + 1);

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        System.out.println("current available balance:" + subAccounts.get(subAccountId).getAvailableBalance().toPlainString());
      }
    }, 1000, 2000);

    coccurrently(range.mapToObj(BigDecimal::new).collect(Collectors.toList()));

    Assert.assertEquals(0, subAccounts.get(subAccountId).getAvailableBalance().compareTo(BigDecimal.valueOf(expected)));
    Assert.assertEquals(100 * 100, subAccounts.get(subAccountId).getFreeze().size());
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  public void freeze(String subAccountId, String orderId, BigDecimal amount) {
    final Object o = subAccounts.executeOnKey(subAccountId, new SubaccountEntryProcessor(orderId, subAccountId, amount));
    Assert.assertTrue((Boolean) o);
  }

}
