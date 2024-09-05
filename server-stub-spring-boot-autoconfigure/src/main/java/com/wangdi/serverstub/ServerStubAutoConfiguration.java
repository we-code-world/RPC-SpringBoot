package com.wangdi.serverstub;

import com.wangdi.serverstub.rpc.Server;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.ServiceRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ServerProperties.class})
public class ServerStubAutoConfiguration {

    @Bean
    Server server(ServerProperties serverProperties, ServiceRegistry serviceRegistry, Serializer serializer){
        return new Server(serverProperties, serviceRegistry, serializer);
    }
}
