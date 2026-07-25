package com.xml.xiaobinnode.chat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 服务器 - 用于实时聊天
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyWebSocketServer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Value("${netty.websocket.port:9090}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // HTTP编解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 大数据流写入支持
                        pipeline.addLast(new ChunkedWriteHandler());
                        // HTTP消息聚合（最大65536字节）
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        // WebSocket协议处理
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 自定义消息处理器
                        pipeline.addLast(chatWebSocketHandler);
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();
        channel = future.channel();
        log.info("Netty WebSocket 服务器启动成功，端口: {}", port);
    }

    @PreDestroy
    public void stop() {
        if (channel != null) {
            channel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("Netty WebSocket 服务器已停止");
    }
}
