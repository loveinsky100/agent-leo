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

public class Udp2TcpHandler extends ChannelInboundHandlerAdapter {
    private static final InternalLogger log;

    static {
        log = InternalLoggerFactory.getInstance(Udp2TcpHandler.class);
    }

    private final XRequestResolver requestResolver;
    private final Wrapper wrapper;

    public Udp2TcpHandler(XRequestResolver requestResolver, Wrapper wrapper) {
        this.requestResolver = requestResolver;
        this.wrapper = wrapper;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagram = (DatagramPacket) msg;
        InetSocketAddress sender = datagram.sender();
        Channel tcpChannel = AgentChannelMapper.getTcpChannel(sender);
        if (tcpChannel == null) {
            // udpSource not registered, actively discard this packet
            // without register, an udp channel cannot relate to any tcp channel, so remove the map
            AgentChannelMapper.removeUdpMapping(sender);
            log.warn("Bad Connection! (unexpected udp datagram from {})", sender);
        } else if (tcpChannel.isActive()) {
            ByteBuf byteBuf = datagram.content();
            try {
                if (!byteBuf.hasArray()) {
                    byte[] bytes = new byte[byteBuf.readableBytes()];
                    byteBuf.getBytes(0, bytes);
                    log.info("\t          Proxy << Target \tFrom   {}:{}", sender.getHostString(), sender.getPort());

                    // write udp payload via tcp channel
                    tcpChannel.writeAndFlush(Unpooled.wrappedBuffer(wrapper.wrap(requestResolver.wrap(XRequest.Channel.UDP, bytes))));
                    log.info("\tClient << Proxy           \tGet [{} bytes]", bytes.length);
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("\tBad Connection! ({})", cause.getMessage());
        AgentChannelMapper.closeChannelGracefullyByUdpChannel(ctx.channel());
    }
}
