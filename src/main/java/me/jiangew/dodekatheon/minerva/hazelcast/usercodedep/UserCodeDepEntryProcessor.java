package me.jiangew.dodekatheon.minerva.hazelcast.usercodedep;

import com.hazelcast.map.AbstractEntryProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author jiangew
 * @description
 */
@Slf4j
public class UserCodeDepEntryProcessor extends AbstractEntryProcessor<Long, UserCodeDepDataModel> {

  @Override
  public Object process(Map.Entry<Long, UserCodeDepDataModel> entry) {
    log.info("Entry before update: {}", entry.getValue().toString());
    entry.setValue(new UserCodeDepDataModel(1, 2, 3, 4));
    log.info("Entry after update: {}", entry.getValue().toString());

    return true;
  }

}
