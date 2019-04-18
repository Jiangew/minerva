package me.jiangew.dodekatheon.minerva.hazelcast.usercodedep;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * @author jiangew
 * @desc
 */
public class UserCodeDepTestClient {

  UserCodeDepTestClient() {
    // ClientConfig clientConfig = new ClientConfig();
    ClientConfig clientConfig = new XmlClientConfigBuilder().build();

    // client user code deployment
    // ClientUserCodeDeploymentConfig clientUserCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
    // clientUserCodeDeploymentConfig.addClass("me.jiangew.dodekatheon.minerva.hazelcast.usercodedep.UserCodeDepDataModel");
    // clientUserCodeDeploymentConfig.addClass("me.jiangew.dodekatheon.minerva.hazelcast.usercodedep.UserCodeDepEntryProcessor");
    // clientUserCodeDeploymentConfig.setEnabled(true);
    // clientConfig.setUserCodeDeploymentConfig(clientUserCodeDeploymentConfig);

     HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
//    HazelcastInstance client = new HazelcastClientFactory(clientConfig).getHazelcastInstance();
    IMap<Long, UserCodeDepDataModel> map = client.getMap("entryProcessor_map");
    // UserCodeDepDataModel old = map.get(1L);
    // if (old != null) {
    //     System.out.println("Entry before update: " + map.get(1L));
    // }
    Object result = map.executeOnKey(1L, new UserCodeDepEntryProcessor());
    System.out.println("EntryProcessor exec result: " + result);
    System.out.println("Entry after update: " + map.get(1L));
  }

  public static void main(String[] args) {
    new UserCodeDepTestClient();
  }

}