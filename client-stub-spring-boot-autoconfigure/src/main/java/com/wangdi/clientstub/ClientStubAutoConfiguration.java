package com.wangdi.clientstub;

import com.wangdi.servicecenter.ServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RemoteCallAspect.class})
public class ClientStubAutoConfiguration {
    @Autowired
    ServiceDiscovery serviceDiscovery;

}
