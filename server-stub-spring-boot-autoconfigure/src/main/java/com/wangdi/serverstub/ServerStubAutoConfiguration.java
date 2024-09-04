package com.wangdi.serverstub;

import com.wangdi.servicecenter.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ServerStubAutoConfiguration {
    @Autowired
    ServiceRegistry serviceRegistry;
}
