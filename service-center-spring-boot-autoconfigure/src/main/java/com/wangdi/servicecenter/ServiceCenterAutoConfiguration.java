package com.wangdi.servicecenter;

import com.wangdi.servicecenter.protostuff.ProtostuffSerialize;
import com.wangdi.servicecenter.zookeeper.ZooKeeperServiceDiscovery;
import com.wangdi.servicecenter.zookeeper.ZooKeeperServiceRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ServiceCenterProperties.class})
public class ServiceCenterAutoConfiguration {

    @Bean
    ServiceDiscovery serviceDiscovery(ServiceCenterProperties serviceCenterProperties){
        return new ZooKeeperServiceDiscovery(serviceCenterProperties);
    }

    @Bean
    ServiceRegistry serviceRegistry(ServiceCenterProperties serviceCenterProperties){
        return new ZooKeeperServiceRegistry(serviceCenterProperties);
    }

    @Bean
    Serializer serializer(ServiceCenterProperties serviceCenterProperties){
        return new ProtostuffSerialize(serviceCenterProperties);
    }
}
