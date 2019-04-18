package me.jiangew.dodekatheon.minerva.hazelcast;

import com.codahale.metrics.Timer;
import com.hazelcast.core.IMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiangew
 * @desc
 */
public class HazelcastCaseTest extends HazelcastBaseTest {

  private static final String ORDERS_MAP_KEY = "t_orders";
  private static final String SUBACCOUNT_MAP_KEY = "t_accounts";

  private final Timer contains = metrics.timer("contains");
  private final Timer lock = metrics.timer("lock");
  private final Timer set = metrics.timer("set");
  private final Timer unlock = metrics.timer("unlock");
  private final Timer put = metrics.timer("put");
  private final Timer get = metrics.timer("get");

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void concurrently() throws InterruptedException {
    super.coccurrently(IntStream.range(1, 1 + 10).mapToObj(BigDecimal::new).collect(Collectors.toList()));
  }

  public void freeze(String subAccountId, String orderId, BigDecimal amount) {
    final IMap<String, BigDecimal> orders = instance.getMap(ORDERS_MAP_KEY);

    final Timer.Context containsContext = contains.time();
    try {
      final boolean check = orders.containsKey(orderId);
      if (check) return;
    } finally {
      containsContext.stop();
    }

    final Timer.Context lockContext = lock.time();
    try {
      orders.lock(orderId);
    } finally {
      lockContext.stop();
    }
    try {
      if (orders.containsKey(orderId)) {
        return;
      }
      final Timer.Context putContext = put.time();
      try {
        orders.put(orderId, amount);
      } finally {
        putContext.stop();
      }

      final IMap<String, BigDecimal> acconts = instance.getMap(SUBACCOUNT_MAP_KEY);

      final Timer.Context lockContext_2 = lock.time();
      try {
        acconts.lock(subAccountId);
      } finally {
        lockContext_2.stop();
      }

      try {

        final BigDecimal bigDecimal;
        final Timer.Context getContext = get.time();
        try {
          bigDecimal = acconts.get(subAccountId);
        } finally {
          getContext.stop();
        }
        final BigDecimal newAmount = plus(bigDecimal, amount);

        final Timer.Context setContext = set.time();
        try {
          acconts.set(subAccountId, newAmount);
        } finally {
          setContext.stop();
        }

      } finally {
        // if (lock.isLocked()) lock.unlock();
        final Timer.Context unlockContext = unlock.time();
        if (acconts.isLocked(subAccountId)) try {
          acconts.unlock(subAccountId);
        } finally {
          unlockContext.stop();
        }
      }
    } finally {
      final Timer.Context unlockContext_2 = unlock.time();
      try {
        orders.unlock(orderId);
      } finally {
        unlockContext_2.stop();
      }
    }
  }

  private BigDecimal plus(BigDecimal source, BigDecimal plus) {
    if (source != null) {
      source.add(plus);
    }
    return plus;
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

}
