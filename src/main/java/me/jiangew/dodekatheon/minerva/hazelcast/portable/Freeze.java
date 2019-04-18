package me.jiangew.dodekatheon.minerva.hazelcast.portable;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class Freeze implements Portable {
  public final static int CLASS_ID = 6;

  public static final Freeze EMPTY = new Freeze("-1000", BigDecimal.valueOf(-1000));

  private String orderId;
  private BigDecimal amount;

  public Freeze() {
  }

  public Freeze(String orderId, BigDecimal amount) {
    this.orderId = orderId;
    this.amount = amount;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
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
    portableWriter.writeUTF("amount", amount.toPlainString());
  }

  @Override
  public void readPortable(PortableReader portableReader) throws IOException {
    this.orderId = portableReader.readUTF("orderId");
    this.amount = new BigDecimal(portableReader.readUTF("amount"));
  }
}
