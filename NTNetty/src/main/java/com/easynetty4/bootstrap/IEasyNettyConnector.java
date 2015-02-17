package com.easynetty4.bootstrap;

import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.channel.InvokeFuture;

public interface IEasyNettyConnector extends IEasyBootstrap {
	public Object writeSync(SocketAddress address, MessageBuf.Message message);

	public InvokeFuture writeAsync(SocketAddress address,
			MessageBuf.Message message);

	public Object writeSync(SocketAddress address, MessageBuf.Message message,
			long timeout, TimeUnit unit);

	ChannelFuture write(SocketAddress address, MessageBuf.Message message);

	void addDisconnectAddress(SocketAddress address);
	
	public ChannelFuture[] connect(SocketAddress... addressArr);
}
