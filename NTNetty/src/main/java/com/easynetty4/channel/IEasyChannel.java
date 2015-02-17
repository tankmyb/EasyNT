package com.easynetty4.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.listener.IChannelInactiveListener;

public interface IEasyChannel {

	public Channel channel();
	public  InvokeFuture writeAsync(MessageBuf.Message message);
	public Object writeSync(MessageBuf.Message message);
	public Object writeSync(MessageBuf.Message message,long timeout,TimeUnit unit);
	public  InvokeFuture removeFuture(long key);
	public ChannelFuture write(MessageBuf.Message message);
	public boolean isActive();
	public ChannelFuture close();
	IEasyBootstrap bootstrap();
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
	
	
}
