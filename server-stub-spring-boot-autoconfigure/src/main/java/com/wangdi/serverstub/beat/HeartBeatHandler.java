package com.wangdi.serverstub.beat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatHandler extends SimpleChannelInboundHandler {
    /** 空闲次数 */
    private int idle_count = 1;
    Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o) throws Exception {
        if (o instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) o;
            if (IdleState.READER_IDLE.equals(event.state())) { // 如果读通道处于空闲状态，说明没有接收到心跳命令
                if (idle_count > 2) {
                    System.out.println("超过两次无客户端请求，关闭该channel");
                    ctx.channel().close();
                }
                idle_count++;
            }
        } else {
            super.userEventTriggered(ctx, o);
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
