package me.jiangew.dodekatheon.minerva.hazelcast.portable;

import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.jiangew.dodekatheon.minerva.hazelcast.protobuf.FreezeProtos;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class SubaccountEntryProcessor extends AbstractEntryProcessor<String, FreezeProtos.Subaccount> implements Portable {
  public static int CLASS_ID = 19;

  private String orderId;
  private String subAccountId;
  private BigDecimal balance;

  public SubaccountEntryProcessor() {
  }

  public SubaccountEntryProcessor(String orderId, String subAccountId, BigDecimal balance) {
    super();
    this.orderId = orderId;
    this.subAccountId = subAccountId;
    this.balance = balance;
  }

  @Override
  public Object process(Map.Entry<String, FreezeProtos.Subaccount> entry) {
    FreezeProtos.Subaccount value = entry.getValue();
    try {
      final FreezeProtos.Subaccount subaccount = freezeNew(orderId, subAccountId, balance, value);
      if (subaccount != null) {
        entry.setValue(subaccount);
      }
      return subaccount;
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
      return false;
    }
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
    portableWriter.writeUTF("orderId", orderId);
    portableWriter.writeUTF("subAccountId", subAccountId);
    portableWriter.writeUTF("balance", balance.toPlainString());
  }

  @Override
  public void readPortable(PortableReader portableReader) throws IOException {
    this.orderId = portableReader.readUTF("orderId");
    this.subAccountId = portableReader.readUTF("subAccountId");
    this.balance = new BigDecimal(portableReader.readUTF("balance"));
  }

  public FreezeProtos.Subaccount freezeNew(
      @NonNull String orderId, @NonNull String subAccountId, @NonNull BigDecimal balance, FreezeProtos.Subaccount subaccount) {
    FreezeProtos.Subaccount.Builder builder = FreezeProtos.Subaccount.newBuilder(subaccount);

    String accountId = subaccount.getSubAccountId();
    Map<String, FreezeProtos.Freeze> freezeMap = subaccount.getFreezeMap();
    Double availableBalance = subaccount.getAvailableBalance();

    if (!subAccountId.equals(accountId)) {
      log.info("subaccountId not equals");
      throw new IllegalArgumentException("Not order for subaccount [" + subAccountId + "]");
    }

    if (freezeMap.containsKey(orderId)) {
      // log.info("duplicate order id {}", orderId);
      return null;
    }

    if (availableBalance > balance.doubleValue()) {
      FreezeProtos.Freeze freeze = FreezeProtos.Freeze.newBuilder().setAmount(balance.doubleValue()).setOrderId(orderId).build();
      builder.putFreeze(orderId, freeze);

      builder.setAvailableBalance(availableBalance - balance.doubleValue());
      // log.info("subaccount builder subaccountId: {}, balance: {}", accountId, builder.getAvailableBalance());

      return builder.build();
    }

    log.info("available balance is not enough", balance);
    return null;
  }

}
