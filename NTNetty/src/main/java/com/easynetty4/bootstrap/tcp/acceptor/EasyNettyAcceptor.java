package com.easynetty4.bootstrap.tcp.acceptor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easynetty4.bootstrap.AEasyBootstrap;
import com.easynetty4.bootstrap.IEasyNettyAcceptor;
import com.easynetty4.bootstrap.tcp.EasyNettyAcceptorHandler;
import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.dispatcher.INettyDispatcher;
import com.easynetty4.dispatcher.NettyDispatcher;
import com.easynetty4.threadpool.AbortPolicyWithReport;
import com.easynetty4.threadpool.NamedThreadFactory;
import com.easynetty4.utils.StackTraceUtil;

public class EasyNettyAcceptor extends AEasyBootstrap implements IEasyNettyAcceptor {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private EventLoopGroup bossGroup;
	public EasyNettyAcceptor(){
		super();
	}
	public EasyNettyAcceptor(int boosGroupCount){
		super(boosGroupCount);
	}
	protected void init(){
		setOption(OptionKey.READ_IDLE_TIME, 180);
		setOption(OptionKey.WRITE_IDLE_TIME, 60);
		super.init();
	}
	@Override
	public void bind(int port) {
		bossGroup = new NioEventLoopGroup(OptionKey.BOSSGROUP.intValue());
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					//.handler(new LoggingHandler(LogLevel.ERROR))
					.childHandler(channelInitializer);
			serverBootstrap.childOption(
					ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024);
			serverBootstrap.childOption(
					ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
			// 设置为pooled的allocator,
			// netty4.0这个版本默认是unpooled,必须设置参数-Dio.netty.allocator.type pooled,
			// 或直接指定pooled
			serverBootstrap.childOption(ChannelOption.ALLOCATOR,
					PooledByteBufAllocator.DEFAULT);
			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			// 直接发包
			serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			logger.error("bind:{} error,ex:{}",port,StackTraceUtil.getStackTrace(e));
		} finally {
			shutdown();
		}

	}

	@Override
	public void shutdown() {
		super.shutdown();
		bossGroup.shutdownGracefully();
	}

	@Override
	public ThreadPoolExecutor serviceExecutor() {
		return serviceExecutor;
	
	}
	@Override
	public ThreadPoolExecutor createServcieExecutor() {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				OptionKey.MAXIMUMPOOLSIZE.intValue(),
				OptionKey.MAXIMUMPOOLSIZE.intValue(), 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(OptionKey.POOLQUEUECAPACITY
						.intValue()), new NamedThreadFactory("serviceExecutor",
						true), new AbortPolicyWithReport("serviceExecutor"));
		return threadPoolExecutor;
	
	}
	@Override
	public ChannelHandlerAdapter initHandler(INettyDispatcher dispatcher) {
		return new EasyNettyAcceptorHandler(dispatcher, this);
	}
}
