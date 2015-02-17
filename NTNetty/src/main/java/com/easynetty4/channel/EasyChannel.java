package com.easynetty4.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.exception.EasyRuntimeException;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.listener.IChannelInactiveListener;

public class EasyChannel implements IEasyChannel{

    private ChannelHandlerContext ctx;
    protected List<IChannelActiveListener> channelActiveListeners;
	protected List<IChannelInactiveListener> channelInactiveListeners;
	private ConcurrentHashMap<Long, InvokeFuture> futures = new ConcurrentHashMap<Long, InvokeFuture>();
	IEasyBootstrap bootstrap;
	public  EasyChannel(ChannelHandlerContext ctx,IEasyBootstrap bootstrap) {
		this.ctx = ctx;
		this.bootstrap = bootstrap;
	}
	@Override
	public Object writeSync(MessageBuf.Message message) {
		InvokeFuture invokeFuture= writeAsync(message);
		return invokeFuture.getResult();
	}
	@Override
	public InvokeFuture writeAsync(MessageBuf.Message message) {
		final long sessionId= message.getHeader().getSid();
		final InvokeFuture invokeFuture = new InvokeFuture();
		invokeFuture.setChannel(this);
		if(!isActive()){
			throw new EasyRuntimeException("连不上服务器！");
		}
		ChannelFuture channelFuture = ctx.writeAndFlush(message);
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					futures.put(sessionId, invokeFuture);
				}else {
					invokeFuture.setCause(future.cause());
				}
				
			}
		});
		return invokeFuture;
	}
	@Override
	public Object writeSync(MessageBuf.Message message, long timeout, TimeUnit unit) {
		InvokeFuture invokeFuture= writeAsync(message);
		return invokeFuture.getResult(timeout,unit);
	}
	@Override
	public Channel channel() {
		return ctx.channel();
	}

	@Override
	public  InvokeFuture removeFuture(long key) {
		return futures.remove(key);
	}
	@Override
	public ChannelFuture write(Message message) {
		return ctx.writeAndFlush(message);
	}
	@Override
	public boolean isActive() {
		return channel()!=null && channel().isActive();
	}
	@Override
	public ChannelFuture close() {
		return ctx.close();
	}
	@Override
	public IEasyBootstrap bootstrap() {
		return bootstrap;
	}
	@Override
	public void addChannelActiveListener(
			IChannelActiveListener channelActiveListener) {
		synchronized (this) {
			if(channelActiveListeners==null){
				channelActiveListeners=new ArrayList<IChannelActiveListener>();
			}
			channelActiveListeners.add(channelActiveListener);
		}
	}
	@Override
	public List<IChannelActiveListener> channelActiveListeners() {
		return channelActiveListeners;
	}
	@Override
	public void addChannelInactiveListener(
			IChannelInactiveListener channelInactiveListener) {
		synchronized (this) {
			if(channelInactiveListeners==null){
				channelInactiveListeners=new ArrayList<IChannelInactiveListener>();
			}
			channelInactiveListeners.add(channelInactiveListener);
		}
	}
	@Override
	public List<IChannelInactiveListener> channelInactiveListeners() {
		return channelInactiveListeners;
	}
}
