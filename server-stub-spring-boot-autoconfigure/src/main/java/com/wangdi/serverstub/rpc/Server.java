package com.wangdi.serverstub.rpc;
import com.wangdi.serverstub.ServerProperties;
import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceRegistry;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
import com.wangdi.servicecenter.entity.RemoteCallResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class Server extends SimpleChannelInboundHandler<RemoteCallRequest> implements ApplicationContextAware, InitializingBean {
    private final int bossNum = 1;
    private final int workerNum = 8;
    Logger logger = LoggerFactory.getLogger(Server.class);
    private Map<RemoteCallService, Object> serviceMap = new HashMap<>();
    private final Server self = this;
    // 监听地址
    private final String address;
    // 监听端口
    private final int port;
    // 服务注册
    private final ServiceRegistry serviceRegistry;

    public Server(ServerProperties serverProperties, ServiceRegistry serviceRegistry) {
        this.address = serverProperties.getAddress();
        this.port = serverProperties.getPort();
        this.serviceRegistry = serviceRegistry;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RemoteCallRequest remoteCallRequest) throws Exception {
        RemoteCallResponse response = new RemoteCallResponse();
        response.setRequestId(remoteCallRequest.getRequestId());
        try {

        }catch (Exception e){
            logger.error("handle error", e);
            response.setException(e);
        }
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossNum);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerNum);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast();
                    channelPipeline.addLast();
                    channelPipeline.addLast(self);
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(address, port).sync();
            serviceMap.forEach((key, value) -> {
                Service service = new Service(key.implementInterface().getName(), key.version(), address, port);
                serviceRegistry.register(service);
                logger.info("register service: {} => {}", key, address);
            });

            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(RemoteCallService.class).forEach((key, value) -> {
            RemoteCallService annotation = value.getClass().getAnnotation(RemoteCallService.class);
            serviceMap.put(annotation, value);
        });
    }
}
