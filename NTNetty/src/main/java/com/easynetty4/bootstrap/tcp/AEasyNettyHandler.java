package com.easynetty4.bootstrap.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.SocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easynetty4.Enum.MessageType;
import com.easynetty4.bean.proto.HeaderBuf;
import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.channel.EasyChannel;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.dispatcher.INettyDispatcher;
import com.easynetty4.listener.IChannelInactiveListener;
import com.easynetty4.utils.StackTraceUtil;

public abstract class AEasyNettyHandler extends
		SimpleChannelInboundHandler<MessageBuf.Message> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected INettyDispatcher dispatcher;
	protected IEasyBootstrap bootstrap;

	public AEasyNettyHandler(INettyDispatcher dispatcher,
			IEasyBootstrap bootstrap) {
		this.dispatcher = dispatcher;
		this.bootstrap = bootstrap;
	}

	/**
	 * 通道建立后资源加载,权限校验等操作
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(ctx.channel().remoteAddress()+" channelActive");
		IEasyChannel easyChannel = new EasyChannel(ctx, bootstrap);
		bootstrap.channels().putIfAbsent(ctx.channel().remoteAddress(),
				easyChannel);
		dispatcher.handleChannelActive(easyChannel);
		super.channelActive(ctx);
	}

	/**
	 * 通道失效后自定义资源的释放
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SocketAddress address =ctx.channel().remoteAddress();
		logger.error(address+" channelInactive");
       final IEasyChannel easyChannel = bootstrap.channels().get(address);
		List<IChannelInactiveListener> inactiveListeners = bootstrap
				.channelInactiveListeners();
		if (inactiveListeners != null) {
			for (IChannelInactiveListener listener : inactiveListeners) {
				listener.channelInactive(easyChannel);
			}
		}
		List<IChannelInactiveListener> channelInactiveListeners = easyChannel
				.channelInactiveListeners();
		if (channelInactiveListeners != null) {
			for (IChannelInactiveListener listener : channelInactiveListeners) {
				listener.channelInactive(easyChannel);
			}
		}
		bootstrap.channels().remove(ctx.channel().remoteAddress());
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error("exceptionCaught:ex:{}",
				StackTraceUtil.getStackTrace(cause));
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {

		/* 心跳处理 */
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				/* 读超时 */
				logger.error("read timeout");
				ctx.disconnect();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				/* 写超时 */
				logger.info("write timeout");
				HeaderBuf.Header.Builder header = HeaderBuf.Header.newBuilder();
				header.setMtype(MessageType.HEARTBEAT.getValue());
				MessageBuf.Message.Builder message = MessageBuf.Message
						.newBuilder();
				message.setHeader(header);
				ctx.writeAndFlush(message.build());
			} else if (event.state() == IdleState.ALL_IDLE) {
				/* 总超时 */
			}
		}
	}

	
}
