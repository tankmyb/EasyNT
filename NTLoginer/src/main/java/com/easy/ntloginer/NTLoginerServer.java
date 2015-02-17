package com.easy.ntloginer;
import java.util.concurrent.ThreadPoolExecutor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.easy.ntdb.dao.IUserDAO;
import com.easynetty4.bootstrap.IEasyNettyConnector;
 
/**
 * A HTTP server showing how to use the HTTP multipart package for file uploads.
 */
public class NTLoginerServer {
 
    private final int port;
    public static boolean isSSL=false;
    ThreadPoolExecutor threadPoolExecutor;
    private IEasyNettyConnector client;
    private IUserDAO userDAO;
    public NTLoginerServer(int port,IEasyNettyConnector client,IUserDAO userDAO,ThreadPoolExecutor threadPoolExecutor) {
        this.port = port;
        this.client = client;
        this.userDAO = userDAO;
        this.threadPoolExecutor = threadPoolExecutor;
    }
 
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NTLoginerInitializer(client,userDAO,threadPoolExecutor));
 
            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
 
}