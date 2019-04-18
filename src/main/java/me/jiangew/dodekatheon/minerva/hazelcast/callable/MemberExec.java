package me.jiangew.dodekatheon.minerva.hazelcast.callable;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

/**
 * @author jiangew
 * @description
 */
public class MemberExec {

  public static void main(String[] args) {
    HazelcastInstance instance = Hazelcast.newHazelcastInstance();
    IExecutorService executorService = instance.getExecutorService("executor");

    ExecutionCallback<Integer> executionCallback = new ExecutionCallback<Integer>() {
      @Override
      public void onResponse(Integer integer) {
        System.out.println("Result: " + integer);
      }

      @Override
      public void onFailure(Throwable throwable) {
        throwable.printStackTrace();
      }
    };

    // submit to somewhere
    executorService.submit(new CallableTask(), executionCallback);
    // executorService.submitToMember(task, member);
    // executorService.submitToKeyOwner(task, key);
    // executorService.submitToMembers(task, members);
  }
}
