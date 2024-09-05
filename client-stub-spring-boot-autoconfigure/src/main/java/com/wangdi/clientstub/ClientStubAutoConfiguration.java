package com.wangdi.clientstub;

import com.wangdi.clientstub.rpc.Client;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.ServiceDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Import({RemoteCallAspect.class, ServiceFactory.class})
public class ClientStubAutoConfiguration {
    Map<String, Client> clientMap = new ConcurrentHashMap<>();

//    @Bean
//    RemoteCallAspect remoteCallAspect(ServiceDiscovery serviceDiscovery, Serializer serializer) {
//        return new RemoteCallAspect(clientMap, serviceDiscovery, serializer);
//    }

    @Bean
    ServiceFactory serviceFactory(ServiceDiscovery serviceDiscovery, Serializer serializer) {
        return new ServiceFactory(clientMap, serviceDiscovery, serializer);
    }

}
