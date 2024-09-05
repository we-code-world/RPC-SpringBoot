package com.wangdi.servicecenter.zookeeper;

import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ZooKeeperServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final ZooKeeperProperties registerCenter;
    Logger logger = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    public ZooKeeperServiceDiscovery(ZooKeeperProperties registerCenter){
        this.registerCenter = registerCenter;
        zkClient = new ZkClient(registerCenter.getAddress(), registerCenter.getSessionTimeout(), registerCenter.getConnectTimeout());
    }

    @Override
    public String loadBalancing(List<String> addresses) {
        return addresses.get(ThreadLocalRandom.current().nextInt(addresses.size()));
    }

    @Override
    public Service discovery(String name, String version) {
        // 获取 registry 节点下所有 service 持久节点
        String servicePath = "/" + registerCenter.getZkRegistryPath() + "/" + name + "/" + version;
        if (!zkClient.exists(servicePath)) {
            logger.info("no such service: {}({})", name, version);
            return null;
        }
        // 在 service 节点下查找 address 临时节点
        List<String> addressList = zkClient.getChildren(servicePath);
        String address = loadBalancing(addressList);
        logger.info("get address node: {}", address);
        String[] addressArray = address.split(":");
        assert addressArray.length == 2;
        return new Service(name, version, addressArray[0], Integer.parseInt(addressArray[1]));
    }
}
