package com.leo.agent.server.net.nio;

import com.leo.agent.protocol.request.XRequest;
import com.leo.agent.protocol.request.XRequestResolver;
import com.leo.agent.wrapper.Wrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;

public final class Tcp2UdpHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger log;

    static {
        log = InternalLoggerFactory.getInstance(AgentRelayHandler.class);
    }

    private final XRequestResolver requestResolver;
    private final Wrapper wrapper;

    private InetSocketAddress udpTarget;

    public Tcp2UdpHandler(InetSocketAddress udpTarget, XRequestResolver requestResolver, Wrapper wrapper) {
        this.udpTarget = udpTarget;
        this.requestResolver = requestResolver;
        this.wrapper = wrapper;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            if (!byteBuf.hasArray()) {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(0, bytes);
                bytes = wrapper.unwrap(bytes);

                // resolve xRequest
                XRequest request = requestResolver.parse(bytes);
                String host = request.getHost();
                int port = request.getPort();
                byte[] content = Arrays.copyOfRange(bytes, bytes.length - request.getSubsequentDataLength(), bytes.length);

                // update udp_target if xRequest.target changes
                if (!(udpTarget.getHostString().equals(host) && udpTarget.getPort() == port)) {
                    log.warn("UDP target changed: {}:{} -> {}:{}, update channel_map", udpTarget.getHostString(), udpTarget.getPort(), host, port);
                    AgentChannelMapper.removeTcpMapping(udpTarget);
                    AgentChannelMapper.removeUdpMapping(udpTarget);
                    udpTarget = new InetSocketAddress(host, port);
                    AgentChannelMapper.putTcpChannel(udpTarget, ctx.channel());
                }
                log.info("\tClient >> Proxy           \tTarget {}:{}", host, port);

                // redirect tcp -> udp
                Channel udpChannel = AgentChannelMapper.getUdpChannel(udpTarget);
                if (udpChannel == null) {
                    log.warn("Bad Connection! (udp channel closed)");
                    AgentChannelMapper.closeChannelGracefullyByTcpChannel(ctx.channel());
                } else if (udpChannel.isActive()) {
                    udpChannel.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(content), udpTarget, UdpServer.getUdpAddr()));
                    log.info("\t          Proxy >> Target \tSend [{} bytes]", content.length);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        AgentChannelMapper.closeChannelGracefullyByTcpChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("\tBad Connection! ({})", cause.getMessage());
        AgentChannelMapper.closeChannelGracefullyByTcpChannel(ctx.channel());
    }
}
