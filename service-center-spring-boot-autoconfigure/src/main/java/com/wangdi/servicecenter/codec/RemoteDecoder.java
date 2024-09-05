package com.wangdi.servicecenter.codec;

import com.wangdi.servicecenter.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RemoteDecoder extends ByteToMessageDecoder {
    private final Class<?> clazz;
    private final Serializer serializer;
    public RemoteDecoder(Class<?> clazz, Serializer serializer){
        this.clazz = clazz;
        this.serializer = serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() <= 4) return;
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if(byteBuf.readableBytes() < length){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] body = new byte[length];
        byteBuf.readBytes(body);
        list.add(serializer.deserialize(body, clazz));
    }
}
