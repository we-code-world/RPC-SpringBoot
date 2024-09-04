package com.wangdi.clientstub.rpc;

import com.wangdi.servicecenter.entity.RemoteCallResponse;
import io.netty.channel.*;

public class Client extends SimpleChannelInboundHandler<RemoteCallResponse> {
    private final String host;
    private final int port;
    public Client(String host, String port){
        this.host = host;
        this.port = Integer.parseInt(port);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RemoteCallResponse remoteCallResponse) throws Exception {

    }
}
