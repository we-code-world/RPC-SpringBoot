package com.wangdi.servicecenter.codec;

import com.wangdi.servicecenter.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RemoteEncoder extends MessageToByteEncoder {
    private final Class<?> clazz;
    private final Serializer serializer;
    public RemoteEncoder(Class<?> clazz, Serializer serializer){
        this.clazz = clazz;
        this.serializer = serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)){
            byte[] body = serializer.serialize(o);
            byteBuf.writeInt(body.length);
            byteBuf.writeBytes(body);
        }
    }
}
