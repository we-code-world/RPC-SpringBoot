package com.wangdi.serverstub.rpc;
import com.wangdi.serverstub.ServerProperties;
import com.wangdi.servicecenter.ServiceRegistry;
import com.wangdi.servicecenter.entity.RemoteCallRequest;
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
    private Map<String, Object> serviceMap = new HashMap<>();
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

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }

    public int listen()

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
            ChannelFuture channelFuture = serverBootstrap.bind(address, Integer.parseInt(port)).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(RemoteCallService.class).entrySet().stream().forEach(entry -> {
            RemoteCallService annotation = entry.getValue().getClass().getAnnotation(RemoteCallService.class);
            serviceMap.put(annotation.implementInterface().getName() + "." + annotation.version(), entry.getValue());
        });
    }
}
