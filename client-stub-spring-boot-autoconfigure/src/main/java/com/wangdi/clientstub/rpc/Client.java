package com.wangdi.clientstub.rpc;

import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceDiscovery;
import com.wangdi.servicecenter.codec.RemoteDecoder;
import com.wangdi.servicecenter.codec.RemoteEncoder;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
import com.wangdi.servicecenter.entity.RemoteCallResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class Client extends SimpleChannelInboundHandler<RemoteCallResponse> {
    private final String host;
    private final int port;
    private final String interfaceName;
    private final String version;
    private final Bootstrap bootstrap;
    private int failedNum;
    private static final long RETRY_TIME = 3;
    private static final int RETRY_COUNT = 3;
    private Map<String, RemoteCallResponse> responseMap = new ConcurrentHashMap<>();


    public Client(ServiceDiscovery serviceDiscovery, Serializer serializer, String interfaceName, String version){
        this.interfaceName = interfaceName;
        this.version = version;
        Service service = serviceDiscovery.discovery(interfaceName, version);
        this.port = service.getPort();
        this.host = service.getAddress();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS));
                    pipeline.addLast(new RemoteEncoder(RemoteCallRequest.class, serializer)); // 编码器
                    pipeline.addLast(new RemoteDecoder(RemoteCallResponse.class, serializer)); // 解码器
                    pipeline.addLast(Client.this); // 处理 RPC 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
        } finally {
            //group.shutdownGracefully();
        }
    }

    public void connect(RemoteCallRequest request) throws InterruptedException {
        // 连接 RPC 服务器
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                final EventLoop eventLoop = channelFuture.channel().eventLoop();
                eventLoop.schedule(() -> {
                    try {
                        if (failedNum < 3) connect(request);
                        else return;
                        failedNum ++;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }, RETRY_TIME, TimeUnit.SECONDS);
            }
        });
        channelFuture.sync();
        // 写入 RPC 请求
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(request).sync();
        // 关闭连接
        channel.closeFuture().sync();
    }


    public Object doCall(RemoteCallRequest request) throws Throwable {
        request.setInterfaceName(interfaceName);
        request.setServiceVersion(version);
        // 连接 RPC 服务器
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.sync();
        // 写入 RPC 请求
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(request).sync();
        // 关闭连接
        channel.closeFuture().sync();
        // 返回 RPC 响应对象
        RemoteCallResponse response = responseMap.get(request.getRequestId());
        if (response == null) {
            throw new RuntimeException("response is null");
        }
        if (response.hasException()) {
            throw response.getException();
        }
        else {
            return response.getResult();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RemoteCallResponse remoteCallResponse) throws Exception {
        responseMap.put(remoteCallResponse.getRequestId(), remoteCallResponse);
    }
}
