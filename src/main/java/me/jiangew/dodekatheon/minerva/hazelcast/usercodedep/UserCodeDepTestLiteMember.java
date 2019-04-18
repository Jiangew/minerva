package me.jiangew.dodekatheon.minerva.hazelcast.usercodedep;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;

/**
 * @author jiangew
 * @desc
 */
public class UserCodeDepTestLiteMember {

  UserCodeDepTestLiteMember() {
    Config config = new Config();
    config.setLicenseKey("ENTERPRISE_HD#4Nodes#IAVJNuk0lrOTEb1wmf7FySjaK65HU3812010191010901121001119080410");
    config.setNetworkConfig(new NetworkConfig().setJoin(new JoinConfig().setMulticastConfig(new MulticastConfig().setMulticastGroup("224.6.6.6"))));
    // config.setManagementCenterConfig(new ManagementCenterConfig("http://localhost:8080/hazelcast-mancenter/", 3));
    config.setLiteMember(true);

    UserCodeDeploymentConfig userCodeDeploymentConfig = config.getUserCodeDeploymentConfig();
    userCodeDeploymentConfig.setEnabled(true)
        // .setClassCacheMode(UserCodeDeploymentConfig.ClassCacheMode.OFF)
        .setProviderMode(UserCodeDeploymentConfig.ProviderMode.LOCAL_AND_CACHED_CLASSES)
        .setWhitelistedPrefixes(
            "me.jiangew.dodekatheon.minerva.hazelcast.usercodedep.UserCodeDepDataModel, me.jiangew.dodekatheon.minerva.hazelcast.usercodedep.UserCodeDepEntryProcessor");

    Hazelcast.newHazelcastInstance(config);
  }

  public static void main(String[] args) {
    new UserCodeDepTestLiteMember();
  }

}
