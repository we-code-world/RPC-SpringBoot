package com.wangdi.servicecenter.zookeeper;

import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
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
        try{
            zkClient = new ZkClient(registerCenter.getAddress(), registerCenter.getSessionTimeout(), registerCenter.getConnectTimeout());
        }catch (ZkTimeoutException e){
            logger.error("zookeeper client startup failed", e);
            throw new RuntimeException("zookeeper client startup timeout", e);
        }
    }

    @Override
    public String loadBalancing(List<String> addresses) {
        if(addresses.isEmpty()) return null;
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
        if(address == null) throw new RuntimeException("no such service provider: " + name);
        logger.info("get address node: {}", address);
        String[] addressArray = address.split(":");
        assert addressArray.length == 2;
        return new Service(name, version, addressArray[0], Integer.parseInt(addressArray[1]));
    }
}
