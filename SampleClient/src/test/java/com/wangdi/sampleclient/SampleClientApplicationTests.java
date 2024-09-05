package com.wangdi.sampleclient;

import com.wangdi.clientstub.ServiceFactory;
import com.wangdi.clientstub.rpc.RemoteCall;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SampleClientApplicationTests {
    @Autowired
    ServiceFactory serviceFactory;
    @Test
    public void helloClient() {
        HelloService helloService = serviceFactory.create(HelloService.class, "default");
        System.out.println(helloService.hello("hhh"));
    }

    @Test
    void contextLoads() {
    }

    @RemoteCall(interfaceName = "")
    String getName(){
        return "";
    }

    @Test
    void testRemoteCallMethod(){}

}
