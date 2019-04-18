package me.jiangew.dodekatheon.minerva.hazelcast.portable;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Subaccount implements Portable {
  public final static int CLASS_ID = 1921;

  private String subAccountId;
  private BigDecimal availableBalance;
  private BigInteger version;
  private Map<String, BigDecimal> freeze;

  public Subaccount() {
  }

  public Subaccount(final String subAccountId,
                    final BigDecimal availableBalance,
                    final BigInteger version) {
    this.subAccountId = subAccountId;
    this.availableBalance = availableBalance;
    this.version = version;
    this.freeze = new HashMap<>();
  }

  public String getSubAccountId() {
    return subAccountId;
  }

  public BigInteger getVersion() {
    return version;
  }

  public BigDecimal getAvailableBalance() {
    return availableBalance;
  }

  public Map<String, BigDecimal> getFreeze() {
    return freeze;
  }

  private boolean hasEnoughAmount(final BigDecimal balance) {
    return this.availableBalance.compareTo(balance) >= 0;
  }

  public boolean freeze(@NonNull String orderId, @NonNull String subAccountId, @NonNull BigDecimal balance) {
    if (!subAccountId.equals(this.subAccountId)) {
      log.warn("except \"{}\", but \"{}\"", this.subAccountId, subAccountId);
      throw new IllegalArgumentException("Not order for subaccount [" + subAccountId + "]");
    }

    if (this.freeze.containsKey(orderId)) {
      log.warn("duplicate order id {}", orderId);
      return false;
    }

    if (this.hasEnoughAmount(balance)) {
      this.freeze.put(orderId, balance);
      this.freezeBalance(balance);
      return true;
    }
    log.warn("available balance is not enough", balance);
    return false;
  }

//    public boolean unfreeze(final UnfreezableResult result) {
//        BigDecimal frozen = this.freeze.get(result.getUnfreezeKey());
//        if (frozen == null) {
//            throw new IllegalArgumentException("Unknown result to unfreeze");
//        }
//        BigDecimal amount = result.getAmount();
//        if (unfreezeBalance(amount)) {
//            if (frozen.compareTo(amount) >= 0) {
//                freeze.put(result.getUnfreezeKey(), frozen.add(amount));
//                return true;
//            }
//        }
//        return false;
//    }

  private boolean unfreezeBalance(BigDecimal balance) {
    if (availableBalance.add(balance).signum() >= 0) {
      this.availableBalance = availableBalance.add(balance);
      return true;
    }
    return false;
  }

  private void freezeBalance(BigDecimal balance) {
    this.availableBalance = availableBalance.subtract(balance);
  }

  @Override
  public int getFactoryId() {
    return 1;
  }

  @Override
  public int getClassId() {
    return CLASS_ID;
  }

  @Override
  public void writePortable(PortableWriter portableWriter) throws IOException {
    portableWriter.writeUTF("subAccountId", subAccountId);
    portableWriter.writeUTF("availableBalance", availableBalance.toPlainString());

    if (freeze.size() == 0) {
      portableWriter.writePortableArray("freeze", new Freeze[]{Freeze.EMPTY});
      return;
    }

    Freeze[] portables = new Freeze[freeze.size()];
    freeze.entrySet().stream().map((Map.Entry<String, BigDecimal> a) -> new Freeze(a.getKey(), a.getValue())).collect(Collectors.toList()).toArray(portables);
    portableWriter.writePortableArray("freeze", portables);
  }

  @Override
  public void readPortable(PortableReader portableReader) throws IOException {
    subAccountId = portableReader.readUTF("subAccountId");
    availableBalance = new BigDecimal(portableReader.readUTF("availableBalance"));

    final Portable[] freezes = portableReader.readPortableArray("freeze");

    Map<String, BigDecimal> freezeMap = new HashMap<>();

    if (freezes != null && freezes.length > 0) {
      for (Portable portable : freezes) {
        Freeze f = (Freeze) portable;

        if (Freeze.EMPTY.getOrderId().equals(f.getOrderId()) && (Freeze.EMPTY.getAmount().compareTo(f.getAmount()) == 0))
          continue;

        freezeMap.put(f.getOrderId(), f.getAmount());
      }
    }
    this.freeze = freezeMap;
  }
}
