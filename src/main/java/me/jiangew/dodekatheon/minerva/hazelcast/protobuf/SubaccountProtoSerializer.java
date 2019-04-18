package me.jiangew.dodekatheon.minerva.hazelcast.protobuf;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

/**
 * @author jiangew
 * @description
 */
public class SubaccountProtoSerializer implements StreamSerializer<FreezeProtos.Subaccount> {

  @Override
  public void write(ObjectDataOutput objectDataOutput, FreezeProtos.Subaccount subaccount) throws IOException {
    objectDataOutput.writeByteArray(subaccount.toByteArray());
  }

  @Override
  public FreezeProtos.Subaccount read(ObjectDataInput objectDataInput) throws IOException {
    return FreezeProtos.Subaccount.parseFrom(objectDataInput.readByteArray());
  }

  @Override
  public int getTypeId() {
    return 0;
  }

  @Override
  public void destroy() {
  }

}
