package com.wangdi.servicecenter;

import com.wangdi.servicecenter.protostuff.ProtostuffSerialize;
import com.wangdi.servicecenter.zookeeper.ZooKeeperProperties;
import com.wangdi.servicecenter.zookeeper.ZooKeeperServiceDiscovery;
import com.wangdi.servicecenter.zookeeper.ZooKeeperServiceRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ZooKeeperProperties.class, ServiceCenterProperties.class})
public class ServiceCenterAutoConfiguration {

    @Bean
    ServiceDiscovery serviceDiscovery(ServiceCenterProperties serviceCenterProperties){
        return new ZooKeeperServiceDiscovery((ZooKeeperProperties) serviceCenterProperties.getRegisterCenter());
    }

    @Bean
    ServiceRegistry serviceRegistry(ServiceCenterProperties serviceCenterProperties){
        return new ZooKeeperServiceRegistry((ZooKeeperProperties) serviceCenterProperties.getRegisterCenter());
    }

    @Bean
    Serializer serializer(ServiceCenterProperties serviceCenterProperties){
        return new ProtostuffSerialize();
    }
}
