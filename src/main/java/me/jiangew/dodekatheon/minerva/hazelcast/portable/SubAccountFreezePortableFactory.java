package me.jiangew.dodekatheon.minerva.hazelcast.portable;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

public class SubAccountFreezePortableFactory implements PortableFactory {

  @Override
  public Portable create(int classId) {
    if (Freeze.CLASS_ID == classId)
      return new Freeze();
    else if (Subaccount.CLASS_ID == classId)
      return new Subaccount();
    else if (SubaccountEntryProcessor.CLASS_ID == classId)
      return new SubaccountEntryProcessor();
    else
      return null;
  }
}
