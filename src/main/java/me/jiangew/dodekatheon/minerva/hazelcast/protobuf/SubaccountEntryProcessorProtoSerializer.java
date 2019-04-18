package me.jiangew.dodekatheon.minerva.hazelcast.protobuf;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

/**
 * @author jiangew
 * @description
 */
public class SubaccountEntryProcessorProtoSerializer implements StreamSerializer<FreezeProtos.SubaccountEntryProcessor> {

  @Override
  public void write(ObjectDataOutput objectDataOutput, FreezeProtos.SubaccountEntryProcessor subaccountEntryProcessor) throws IOException {
    objectDataOutput.writeByteArray(subaccountEntryProcessor.toByteArray());
  }

  @Override
  public FreezeProtos.SubaccountEntryProcessor read(ObjectDataInput objectDataInput) throws IOException {
    return FreezeProtos.SubaccountEntryProcessor.parseFrom(objectDataInput.readByteArray());
  }

  @Override
  public int getTypeId() {
    return 0;
  }

  @Override
  public void destroy() {

  }
}
