package me.jiangew.dodekatheon.minerva.hazelcast.callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author jiangew
 * @description
 */
public class CallableTask implements Callable<Integer>, Serializable, HazelcastInstanceAware {

  private transient HazelcastInstance hazelcastInstance;

  @Override
  public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  @Override
  public Integer call() throws Exception {
    IMap<String, Integer> map = hazelcastInstance.getMap("callable_map");
    int result = 0;
    for (String key : map.localKeySet()) {
      System.out.println("Calculating for key: " + key);
      result += map.get(key);

      System.out.println(hazelcastInstance.getCluster().getLocalMember().toString() + ", key:" + key + ", value:" + map.get(key));
    }

    System.out.println("Local result: " + result);

    return result;
  }
}
