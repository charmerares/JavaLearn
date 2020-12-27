package com.iris.demo.NettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    public void start() {
        new Thread(() -> {
            ServerBootstrap bootstrap = new ServerBootstrap();

            EventLoopGroup boss = new NioEventLoopGroup(1);
            EventLoopGroup worker = new NioEventLoopGroup();

            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringEncoder(),
                                    new StringDecoder()
                                    );
                        }
                    });
            ChannelFuture future;
            try {
                future = bootstrap.bind(9000).sync();
                if (future.isSuccess()) {

                    System.out.println("服务端开启成功");
                } else {
                    System.out.println("服务端开启失败");
                }

                //等待服务监听端口关闭,就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                //优雅地退出，释放线程池资源
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }

    }).start();
    }
}
