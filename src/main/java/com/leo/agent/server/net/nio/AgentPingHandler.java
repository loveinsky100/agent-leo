package com.leo.agent.server.net.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class AgentPingHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(AgentPingHandler.class);

    private final Promise<Channel> promise;
    private final long initializeTimeMillis;

    public AgentPingHandler(Promise<Channel> promise, long initializeTimeMillis) {
        log.info("\t          Proxy -> Target \tPing");
        this.promise = promise;
        this.initializeTimeMillis = initializeTimeMillis;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("\t          Proxy <- Target \tPong ~{}ms", System.currentTimeMillis() - initializeTimeMillis);
        ctx.pipeline().remove(this);
        promise.setSuccess(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        log.warn("\tBad Ping! ({})", throwable.getMessage());
        promise.setFailure(throwable);
    }
}
