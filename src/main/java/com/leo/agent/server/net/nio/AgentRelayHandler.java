package com.leo.agent.server.net.nio;

import com.leo.agent.wrapper.Wrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class AgentRelayHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(AgentRelayHandler.class);

    private final Channel dstChannel;
    private final Wrapper wrapper;
    private final boolean uplink;

    public AgentRelayHandler(Channel dstChannel, Wrapper wrapper, boolean uplink) {
        this.dstChannel = dstChannel;
        this.wrapper = wrapper;
        this.uplink = uplink;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (dstChannel.isActive()) {
            ByteBuf byteBuf = (ByteBuf) msg;
            try {
                if (!byteBuf.hasArray()) {
                    byte[] bytes = new byte[byteBuf.readableBytes()];
                    byteBuf.getBytes(0, bytes);
                    if (uplink) {
                        bytes = wrapper.unwrap(bytes);
                        if (bytes != null) {
                            dstChannel.writeAndFlush(Unpooled.wrappedBuffer(bytes));
                            log.info("\tClient ==========> Target \tSend [{} bytes]", bytes.length);
                        }
                    } else {
                        dstChannel.writeAndFlush(Unpooled.wrappedBuffer(wrapper.wrap(bytes)));
                        log.info("\tClient <========== Target \tGet [{} bytes]", bytes.length);
                    }
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (dstChannel.isActive()) {
            if (!uplink) {
                log.info("\t          Proxy <- Target \tDisconnect");
                log.info("\tClient <- Proxy           \tDisconnect");
            }
            dstChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("\t          Proxy <- Target \tDisconnect");
        log.info("\tClient <- Proxy           \tDisconnect");
        ctx.close();
    }
}