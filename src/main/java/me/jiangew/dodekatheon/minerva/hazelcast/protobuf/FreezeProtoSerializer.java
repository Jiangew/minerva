package me.jiangew.dodekatheon.minerva.hazelcast.protobuf;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

/**
 * @author jiangew
 * @description
 */
public class FreezeProtoSerializer implements StreamSerializer<FreezeProtos.Freeze> {

  @Override
  public void write(ObjectDataOutput objectDataOutput, FreezeProtos.Freeze freeze) throws IOException {
    objectDataOutput.writeByteArray(freeze.toByteArray());
  }

  @Override
  public FreezeProtos.Freeze read(ObjectDataInput objectDataInput) throws IOException {
    return FreezeProtos.Freeze.parseFrom(objectDataInput.readByteArray());
  }

  @Override
  public int getTypeId() {
    return 0;
  }

  @Override
  public void destroy() {

  }
}
