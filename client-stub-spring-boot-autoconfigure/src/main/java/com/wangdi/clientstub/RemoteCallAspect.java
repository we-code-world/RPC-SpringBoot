package com.wangdi.clientstub;

import com.wangdi.clientstub.rpc.Client;
import com.wangdi.clientstub.rpc.RemoteCall;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.ServiceDiscovery;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
public class RemoteCallAspect {
    private final Map<String, Client> clientMap;
    private final ServiceDiscovery serviceDiscovery;
    private final Serializer serializer;

    RemoteCallAspect(Map<String, Client> clientMap, ServiceDiscovery serviceDiscovery, Serializer serializer){
        this.clientMap = clientMap;
        this.serviceDiscovery = serviceDiscovery;
        this.serializer = serializer;
    }

    @Around("@annotation(com.wangdi.clientstub.rpc.RemoteCall)")
    public Object call(ProceedingJoinPoint pjp) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = methodSignature.getMethod();
        RemoteCall annotation = targetMethod.getAnnotation(RemoteCall.class);
        String interfaceName = annotation.interfaceName();
        String version = annotation.version();
        RemoteCallRequest request = new RemoteCallRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(interfaceName);
        request.setServiceVersion(version);
        request.setMethodName(targetMethod.getName());
        request.setParameterTypes(targetMethod.getParameterTypes());
        request.setParameters(pjp.getArgs());
        String clientName = interfaceName + "-" + version;
        Client client = clientMap.getOrDefault(interfaceName, null);
        if(client == null)
            clientMap.put(clientName, client = new Client(serviceDiscovery, serializer, interfaceName, version));
        return client.doCall(request);
    }
}
