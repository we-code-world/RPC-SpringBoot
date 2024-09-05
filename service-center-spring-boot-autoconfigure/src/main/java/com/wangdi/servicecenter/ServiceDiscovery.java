package com.wangdi.servicecenter;

import java.util.List;

public interface ServiceDiscovery {
    String loadBalancing(List<String> addresses);
    Service discovery(String name, String version);
}
