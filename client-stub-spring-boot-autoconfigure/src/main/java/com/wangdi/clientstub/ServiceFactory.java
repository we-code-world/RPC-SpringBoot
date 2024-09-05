package com.wangdi.clientstub;


import com.wangdi.clientstub.rpc.Client;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.ServiceDiscovery;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
import com.wangdi.servicecenter.entity.RemoteCallResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

public class ServiceFactory {
    private final Map<String, Client> clientMap;
    private final ServiceDiscovery serviceDiscovery;
    private final Serializer serializer;

    public ServiceFactory(Map<String, Client> clientMap, ServiceDiscovery serviceDiscovery, Serializer serializer){
        this.clientMap = clientMap;
        this.serviceDiscovery = serviceDiscovery;
        this.serializer = serializer;
    }
    public class RemoteCallInvocationHandler implements InvocationHandler {
        private final Client client;

        public RemoteCallInvocationHandler(Client client) {
            this.client = client;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RemoteCallRequest request = new RemoteCallRequest();
            request.setRequestId(UUID.randomUUID().toString());
            return client.doCall(request);
        }
    }
    public <T> T create(final Class<T> interfaceClass, final String version){
        String interfaceName = interfaceClass.getName();
        String clientName = interfaceName + "-" + version;
        Client client = clientMap.getOrDefault(clientName, null);
        if(client == null)
            clientMap.put(clientName, client = new Client(serviceDiscovery, serializer, interfaceName, version));
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                                            new Class[]{interfaceClass},
                                            new RemoteCallInvocationHandler(client));
    }
}
