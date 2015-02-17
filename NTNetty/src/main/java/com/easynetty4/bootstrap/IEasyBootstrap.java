package com.easynetty4.bootstrap;

import io.netty.channel.ChannelHandlerAdapter;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.context.EasyContext;
import com.easynetty4.dispatcher.INettyDispatcher;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.listener.IChannelInactiveListener;

public interface IEasyBootstrap {

	ConcurrentHashMap<SocketAddress, IEasyChannel> channels();
	EasyContext context();
	void setOption(OptionKey key,Object value);
	Object getOption(OptionKey key);
	ThreadPoolExecutor serviceExecutor();
	public void shutdown();
	/**
	 * 增加通道建立成功后执行的listener
	 * @param channelActiveListener 通道成功后执行的listener
	 */
	void addChannelActiveListener(IChannelActiveListener channelActiveListener);
	/**
	 * 返回所有通道建立成功后执行的listener
	 * @return
	 */
	List<IChannelActiveListener> channelActiveListeners();
	
	void addChannelInactiveListener(
			IChannelInactiveListener channelInactiveListener);
	List<IChannelInactiveListener> channelInactiveListeners();
	
	ChannelHandlerAdapter initHandler(INettyDispatcher dispatcher);
}
