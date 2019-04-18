package me.jiangew.dodekatheon.minerva.hazelcast.usercodedep;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

/**
 * @author jiangew
 * @desc
 */
public class UserCodeDepDataModel implements DataSerializable {

  private long field1;
  private long field2;
  private long field3;
  private long field4;

  public UserCodeDepDataModel() {
  }

  public UserCodeDepDataModel(long field1, long field2, long field3, long field4) {
    this.field1 = field1;
    this.field2 = field2;
    this.field3 = field3;
    this.field4 = field4;
  }

  @Override
  public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
    objectDataOutput.writeLong(field1);
    objectDataOutput.writeLong(field2);
    objectDataOutput.writeLong(field3);
    objectDataOutput.writeLong(field4);
  }

  @Override
  public void readData(ObjectDataInput objectDataInput) throws IOException {
    this.field1 = objectDataInput.readLong();
    this.field2 = objectDataInput.readLong();
    this.field3 = objectDataInput.readLong();
    this.field4 = objectDataInput.readLong();
  }

  public long getField1() {
    return field1;
  }

  public long getField2() {
    return field2;
  }

  public long getField3() {
    return field3;
  }

  public long getField4() {
    return field4;
  }

  // @Override
  // public String toString() {
  //     return "UserCodeDepDataModel{" + "field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + '}';
  // }

  @Override
  public String toString() {
    return "UserCodeDepDataModel{" + "field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + '}';
  }
}
