package com.wangdi.servicecenter.zookeeper;

import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperServiceRegistry implements ServiceRegistry {
    Logger logger = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);
    private final ZkClient zkClient;
    private ZooKeeperProperties registerCenter;
    public ZooKeeperServiceRegistry(ZooKeeperProperties registerCenter) {
        this.registerCenter = registerCenter;
        zkClient = new ZkClient(registerCenter.getAddress(), registerCenter.getSessionTimeout(), registerCenter.getConnectTimeout());
        // 创建 registry 持久节点，该节点下存放所有的 service 节点
        String registryPath = registerCenter.getZkRegistryPath();
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            logger.info("create registry node: {}", registryPath);
        }
    }

    @Override
    public void register(Service service) {
        // 在 registry 节点下创建 service 持久节点，存放服务名称
        String servicePath = registerCenter.getZkRegistryPath() + "/" + service.getName() + "/" + service.getVersion();
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            logger.info("create service node: {}", servicePath);
        }
        // 在 service 节点下创建 address 临时节点,存放服务地址
        zkClient.createEphemeral(servicePath + "/" + service.getAddress() + ":" + service.getPort());
        logger.info("create address node: {}", service.getAddress() + ":" + service.getPort());
    }
}
