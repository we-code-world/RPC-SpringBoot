package com.wangdi.clientstub.rpc;

import com.wangdi.servicecenter.entity.RemoteCallRequest;
import io.netty.channel.*;

public class Client extends SimpleChannelInboundHandler<RemoteCallRequest> {
    private final String host;
    private final int port;
    public Client(String host, String port){
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RemoteCallRequest remoteCallRequest) throws Exception {

    }
}
