package com.wangdi.clientstub;

import com.wangdi.clientstub.rpc.RemoteCall;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class RemoteCallAspect {

    @Around("@annotation(com.wangdi.clientstub.rpc.RemoteCall)")
    public Object call(ProceedingJoinPoint pjp) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method targetMethod = pjp.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        RemoteCall annotation = targetMethod.getAnnotation(RemoteCall.class);
        String version = annotation.version();
    }
}
