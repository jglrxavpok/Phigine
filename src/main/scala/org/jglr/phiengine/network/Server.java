package org.jglr.phiengine.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Server implements Runnable {
    private final NetworkHandler netHandler;
    private Channel channel;
    private int port;
    private Thread thread;

    public Server(NetworkHandler networkHandler) {
        this.netHandler = networkHandler;
    }

    public void start(int port) {
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addFirst(new LengthFieldBasedFrameDecoder(2*1024*1024,0,4)).addLast(new MessageDecoder()).addLast(new MessageEncoder(NetworkSide.SERVER));
                            netHandler.getChannelRegistry().foreachValue(ch.pipeline()::addLast);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).addListener(future -> {
                    // TODO: listener ?
                }
            ).sync();
            channel = f.channel();
            channel.closeFuture().sync();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
