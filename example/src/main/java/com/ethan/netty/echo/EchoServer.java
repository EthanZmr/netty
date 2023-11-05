package com.ethan.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.example.util.ServerUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class EchoServer {
    private static final int port = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws CertificateException, SSLException {
        final SslContext sslContext = ServerUtil.buildSslContext();

        EventLoopGroup boss = new NioEventLoopGroup();
        final EchoServerHandler serverHandler = new EchoServerHandler();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslContext != null) {
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }
                            pipeline.addLast(serverHandler);
                        }
                    });
            // start the server
            ChannelFuture f = bootstrap.bind(port).sync();
            // wait until the server socket is closed
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            boss.shutdownGracefully();
        }
    }


}
