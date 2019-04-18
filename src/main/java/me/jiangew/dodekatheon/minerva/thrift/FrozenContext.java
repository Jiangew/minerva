package me.jiangew.dodekatheon.minerva.thrift;

import io.airlift.drift.annotations.ThriftConstructor;
import io.airlift.drift.annotations.ThriftField;
import io.airlift.drift.annotations.ThriftStruct;

import java.util.Set;

/**
 * @author jiangew
 * @description
 */
@ThriftStruct
public class FrozenContext {

  @ThriftField(1)
  public String category;

  @ThriftField(2)
  public String orderId;

  @ThriftField(3)
  public String subAccountId;

  @ThriftField(4)
  public Integer balance;

  @ThriftField(5)
  public Set<String> transactions;

  @ThriftConstructor
  public FrozenContext(
      final String category, final String orderId, final String subAccountId, final Integer balance, final Set<String> transactions) {
    this.category = category;
    this.orderId = orderId;
    this.subAccountId = subAccountId;
    this.balance = balance;
    this.transactions = transactions;
  }

  public String getFrozenKey() {
    return this.category + ":" + this.orderId;
  }

  public String getSubAccountId() {
    return subAccountId;
  }

  public Integer getBalance() {
    return balance;
  }

}