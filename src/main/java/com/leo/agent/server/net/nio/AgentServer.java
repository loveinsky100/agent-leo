package com.leo.agent.server.net.nio;

import com.leo.agent.Constants;
import com.leo.agent.server.Configuration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.util.concurrent.Executors;

public final class AgentServer {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(AgentServer.class);

    private AgentServer() {
    }

    public static AgentServer getInstance() {
        return new AgentServer();
    }

    public void start() {
        Configuration config = Configuration.INSTANCE;
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
//                                    .addLast("logging", new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new AgentConnectHandler());
                            if (config.getReadLimit() != 0 || config.getWriteLimit() != 0) {
                                socketChannel.pipeline().addLast(
                                        new GlobalTrafficShapingHandler(Executors.newScheduledThreadPool(1), config.getWriteLimit(), config.getReadLimit())
                                );
                            }
                        }
                    });
            log.info("\tStartup {}-{}-server [{}]", Constants.APP_NAME, Constants.APP_VERSION, config.getProtocol());
            new Thread(() -> new UdpServer().start()).start();
            ChannelFuture future = bootstrap.bind(config.getHost(), config.getPort()).sync();
            future.addListener(future1 -> log.info("\tTCP listening at {}:{}...", config.getHost(), config.getPort()));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("\tSocket bind failure ({})", e.getMessage());
        } finally {
            log.info("\tShutting down and recycling...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            Configuration.shutdownRelays();
        }
        System.exit(0);
    }
}
