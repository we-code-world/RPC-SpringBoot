package com.wangdi.serverstub.rpc;
import com.wangdi.serverstub.ServerProperties;
import com.wangdi.servicecenter.Serializer;
import com.wangdi.servicecenter.Service;
import com.wangdi.servicecenter.ServiceRegistry;
import com.wangdi.servicecenter.codec.RemoteDecoder;
import com.wangdi.servicecenter.codec.RemoteEncoder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
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

    private Serializer serializer;

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
            Object bean = serviceMap.get(remoteCallRequest.getInterfaceName() + "-" + remoteCallRequest.getServiceVersion());
            // 获取反射调用所需的参数
            Class<?> serviceClass = bean.getClass();
            String methodName = remoteCallRequest.getMethodName();
            Object[] parameters = remoteCallRequest.getParameters();
            Class<?>[] parameterTypes = remoteCallRequest.getParameterTypes();
            // 通过反射调用客户端请求的方法
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            response.setResult(method.invoke(bean, parameters));
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
                    channelPipeline.addLast(new RemoteDecoder(RemoteCallRequest.class, serializer));
                    channelPipeline.addLast(new RemoteEncoder(RemoteCallResponse.class, serializer));
                    channelPipeline.addLast(self);
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(address, port).sync();
            serviceMap.forEach((key, value) -> {
                String[] serviceVersion = key.split("-");
                assert serviceVersion.length == 2;
                Service service = new Service(serviceVersion[0], serviceVersion[1], address, port);
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
        applicationContext.getBeansOfType(Serializer.class).forEach((k, value) -> {
            if("serializer".equals(k)) serializer = value;
        });
        applicationContext.getBeansWithAnnotation(RemoteCallService.class).forEach((key, value) -> {
            RemoteCallService annotation = value.getClass().getAnnotation(RemoteCallService.class);
            String serviceVersion = annotation.implementInterface().getName() + "-" + annotation.version();
            serviceMap.put(serviceVersion, value);
        });
    }
}
