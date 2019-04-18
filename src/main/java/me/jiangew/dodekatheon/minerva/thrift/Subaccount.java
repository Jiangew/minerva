package me.jiangew.dodekatheon.minerva.thrift;

import io.airlift.drift.annotations.ThriftConstructor;
import io.airlift.drift.annotations.ThriftField;
import io.airlift.drift.annotations.ThriftStruct;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangew
 * @description
 */
@ThriftStruct
public final class Subaccount {

  @ThriftField(1)
  public String subaccountId;

  @ThriftField(2)
  public Integer availableBalance;

  @ThriftField(3)
  public Integer version;

  @ThriftField(4)
  public Map<String, FrozenContext> contexts;

  @ThriftConstructor
  public Subaccount(
      final String subaccountId, final Integer availableBalance, final Integer version) {
    this.subaccountId = subaccountId;
    this.availableBalance = availableBalance;
    this.version = version;
    this.contexts = new HashMap<>();
  }

  public Map<String, FrozenContext> getContexts() {
    return contexts;
  }

  public void setContexts(Map<String, FrozenContext> contexts) {
    this.contexts = contexts;
  }

  public String getSubaccountId() {
    return subaccountId;
  }

  public Integer getAvailableBalance() {
    return availableBalance;
  }

}