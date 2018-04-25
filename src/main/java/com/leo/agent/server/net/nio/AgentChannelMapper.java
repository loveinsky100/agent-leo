package com.leo.agent.server.net.nio;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.net.InetSocketAddress;

public class AgentChannelMapper {
    private static final InternalLogger log;

    static {
        log = InternalLoggerFactory.getInstance(AgentChannelMapper.class);
    }

    private static BiMap<InetSocketAddress, Channel> udpTable = HashBiMap.create();
    private static BiMap<InetSocketAddress, Channel> tcpTable = HashBiMap.create();

    static void putTcpChannel(InetSocketAddress udpTarget, Channel tcpChannel) {
        tcpTable.put(udpTarget, tcpChannel);
    }

    static void putUdpChannel(InetSocketAddress udpTarget, Channel udpChannel) {
        udpTable.put(udpTarget, udpChannel);
    }

    static InetSocketAddress getUdpTargetByTcpChannel(Channel tcpChannel) {
        return tcpTable.inverse().get(tcpChannel);
    }

    static InetSocketAddress getUdpTargetByUdpChannel(Channel udpChannel) {
        return udpTable.inverse().get(udpChannel);
    }

    static Channel getUdpChannelByTcpChannel(Channel tcpChannel) {
        return udpTable.get(getUdpTargetByTcpChannel(tcpChannel));
    }

    static Channel getTcpChannelByUdpChannel(Channel udpChannel) {
        return tcpTable.get(getUdpTargetByUdpChannel(udpChannel));
    }

    static Channel getTcpChannel(InetSocketAddress udpTarget) {
        return tcpTable.get(udpTarget);
    }

    static Channel getUdpChannel(InetSocketAddress udpTarget) {
        return udpTable.get(udpTarget);
    }

    static Channel removeUdpMapping(InetSocketAddress udpTarget) {
        return udpTable.remove(udpTarget);
    }

    static Channel removeTcpMapping(InetSocketAddress udpTarget) {
        return tcpTable.remove(udpTarget);
    }

    static void closeChannelGracefully(InetSocketAddress udpSource) {
        Channel udpChannel = removeUdpMapping(udpSource);
        Channel tcpChannel = removeTcpMapping(udpSource);
        if (udpChannel.isActive()) {
            log.info("\t          Proxy << Target \tDisconnect");
            udpChannel.close();
        }
        if (tcpChannel.isActive()) {
            tcpChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            log.info("\tClient << Proxy           \tDisconnect");
        }
    }

    static void closeChannelGracefullyByTcpChannel(Channel tcpChannel) {
        closeChannelGracefully(getUdpTargetByTcpChannel(tcpChannel));
    }

    static void closeChannelGracefullyByUdpChannel(Channel udpChannel) {
        closeChannelGracefully(getUdpTargetByUdpChannel(udpChannel));
    }
}
