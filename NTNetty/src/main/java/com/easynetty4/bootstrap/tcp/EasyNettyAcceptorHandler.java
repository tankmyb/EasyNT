package com.easynetty4.bootstrap.tcp;

import io.netty.channel.ChannelHandlerContext;

import com.easynetty4.Enum.MessageType;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.dispatcher.INettyDispatcher;

public class EasyNettyAcceptorHandler extends AEasyNettyHandler {
	public EasyNettyAcceptorHandler(INettyDispatcher dispatcher,
			IEasyBootstrap bootstrap) {
		super(dispatcher, bootstrap);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message message)
			throws Exception {
		//System.out.println(message+"==============");
		if (message.getHeader().getMtype() == MessageType.HEARTBEAT.getValue()) {
			return;
		}
		dispatcher.dispatchMessageReceived(message,
				bootstrap.channels().get(ctx.channel().remoteAddress()));

	}
}
