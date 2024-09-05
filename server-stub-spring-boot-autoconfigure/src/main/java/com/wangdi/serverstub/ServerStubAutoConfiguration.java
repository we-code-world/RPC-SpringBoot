package com.wangdi.serverstub;

import com.wangdi.serverstub.rpc.Server;
import com.wangdi.servicecenter.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties({ServerProperties.class})
public class ServerStubAutoConfiguration {

    @Bean
    Server server(ServerProperties serverProperties, ServiceRegistry serviceRegistry){
        return new Server(serverProperties, serviceRegistry);
    }
}
