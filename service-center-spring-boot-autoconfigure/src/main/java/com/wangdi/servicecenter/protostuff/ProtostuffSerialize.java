package com.wangdi.servicecenter.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.ServiceCenterProperties;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
import com.wangdi.servicecenter.entity.RemoteCallResponse;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerialize implements Serializer {
    // 缓存 Schema
    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    private static final Objenesis objenesis = new ObjenesisStd(true);

    public ProtostuffSerialize() {
        Schema<RemoteCallRequest> schemaRequest = RuntimeSchema.createFrom(RemoteCallRequest.class);
        cachedSchema.put(RemoteCallRequest.class, schemaRequest);
        Schema<RemoteCallResponse>  schemaResponse = RuntimeSchema.createFrom(RemoteCallResponse.class);
        cachedSchema.put(RemoteCallResponse.class, schemaResponse);
    }

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = (Schema<T>) cachedSchema.getOrDefault(cls, null);
            if(schema == null){
                schema = RuntimeSchema.createFrom(cls);
                cachedSchema.put(cls, schema);
            }
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(), e);
        }finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T obj = objenesis.newInstance(cls);
            Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
            if (schema == null) throw new RuntimeException("schema did not exist");
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
