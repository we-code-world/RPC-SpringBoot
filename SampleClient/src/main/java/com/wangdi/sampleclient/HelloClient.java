package com.wangdi.sampleclient;

import com.wangdi.clientstub.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class HelloClient {
    @Autowired
    ServiceFactory serviceFactory;
    public HelloClient() {
        /*HelloService helloService = serviceFactory.create(HelloService.class, "default");
        helloService.hello("hhh");*/
    }
}
