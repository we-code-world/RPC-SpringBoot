package com.wangdi.sampleserver;


import com.wangdi.serverstub.rpc.RemoteCallService;
import org.springframework.stereotype.Controller;

@RemoteCallService(implementInterface = HelloService.class)
@Controller("/")
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(String name) {
        return "impl hello " + name;
    }
}
