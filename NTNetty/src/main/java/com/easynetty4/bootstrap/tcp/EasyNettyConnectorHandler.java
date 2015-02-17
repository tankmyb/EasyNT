package com.easynetty4.bootstrap.tcp;

import io.netty.channel.ChannelHandlerContext;

import com.easynetty4.Enum.MessageType;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.channel.InvokeFuture;
import com.easynetty4.dispatcher.INettyDispatcher;

public class EasyNettyConnectorHandler extends AEasyNettyHandler {
	public EasyNettyConnectorHandler(INettyDispatcher dispatcher,
			IEasyBootstrap bootstrap) {
		super(dispatcher, bootstrap);
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Message message)
			throws Exception {
		if (message.getHeader().getMtype() == MessageType.HEARTBEAT.getValue()) {
			return;
		}
		//System.out.println("=========client==="+message.getHeader().getMtype());
		if (message.getHeader().getMtype() == MessageType.SERVICE_HANDLE.getValue()) {
			dispatcher.dispatchMessageReceived(message, bootstrap.channels()
					.get(ctx.channel().remoteAddress()));
		} else {
			long sid = message.getHeader().getSid();
			 //System.out.println(sid + "======sid");
			IEasyChannel easyChannel = bootstrap.channels().get(
					ctx.channel().remoteAddress());
			InvokeFuture invokeFuture = easyChannel.removeFuture(sid);
			if (null != invokeFuture) {
				invokeFuture.setResult(message);
			}

		}

	}
}
