package com.wangdi.sampleclient;

import com.wangdi.clientstub.rpc.RemoteCall;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SampleClientApplicationTests {

    @Test
    void contextLoads() {
    }

    @RemoteCall
    String getName(){
        return "";
    }

    @Test
    void testRemoteCallMethod(){}

}
