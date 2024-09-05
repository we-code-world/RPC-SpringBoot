package com.wangdi.servicecenter;

import com.wangdi.servicecenter.zookeeper.ZooKeeperProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "server-center"
)
public class ServiceCenterProperties {
    private RegisterCenter registerCenter = new ZooKeeperProperties();

    private String name = "center";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RegisterCenter getRegisterCenter() {
        return registerCenter;
    }

    public void setRegisterCenter(RegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
    }

    public static class RegisterCenter{
        String address = "127.0.0.1:8099";

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
