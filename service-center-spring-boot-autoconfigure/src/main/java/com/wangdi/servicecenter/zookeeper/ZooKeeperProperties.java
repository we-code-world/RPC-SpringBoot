package com.wangdi.servicecenter.zookeeper;

import com.wangdi.servicecenter.ServiceCenterProperties.RegisterCenter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "zookeeper"
)
public class ZooKeeperProperties extends RegisterCenter {

    private int sessionTimeout = 5000;

    private int connectTimeout = 1000;

    private String zkRegistryPath = "/registry";

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getZkRegistryPath() {
        return zkRegistryPath;
    }

    public void setZkRegistryPath(String zkRegistryPath) {
        this.zkRegistryPath = zkRegistryPath;
    }
}
