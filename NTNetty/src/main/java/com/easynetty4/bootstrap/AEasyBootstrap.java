package com.easynetty4.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.context.EasyContext;
import com.easynetty4.dispatcher.INettyDispatcher;
import com.easynetty4.dispatcher.NettyDispatcher;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.listener.IChannelInactiveListener;

public abstract class AEasyBootstrap implements IEasyBootstrap {

	protected ConcurrentHashMap<SocketAddress, IEasyChannel> channels = new ConcurrentHashMap<SocketAddress, IEasyChannel>();
	protected EasyContext context = new EasyContext();
	protected ThreadPoolExecutor serviceExecutor;
	protected EventLoopGroup workerGroup;
	protected ChannelInitializer<Channel> channelInitializer;
	protected List<IChannelActiveListener> channelActiveListeners;
	protected List<IChannelInactiveListener> channelInactiveListeners;
	public AEasyBootstrap() {
		this(OptionKey.WORKERGROUP.intValue());
	}


	public AEasyBootstrap(int workerThreadCount) {
		this(workerThreadCount,OptionKey.MAXIMUMPOOLSIZE.intValue());
	}

	public AEasyBootstrap(int workerThreadCount, int serviceCount) {
		setOption(OptionKey.MAXIMUMPOOLSIZE, serviceCount);
		workerGroup = new NioEventLoopGroup(workerThreadCount);
		init();
	}

	protected void init() {
		serviceExecutor = createServcieExecutor();
		channelInitializer = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				final INettyDispatcher dispatcher = new NettyDispatcher(serviceExecutor);
				ChannelPipeline pipeline = ch.pipeline();
				//pipeline.addLast("frameEncoder",
						//new ProtobufVarint32LengthFieldPrepender());
				
				pipeline.addLast("frameEncoder",
						new LengthFieldPrepender(4));
				pipeline.addLast("encoder", new ProtobufEncoder());
				//pipeline.addLast("frameDecoder",
						//new ProtobufVarint32FrameDecoder());
				pipeline.addLast("frameDecoder", new
						 LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
				pipeline.addLast("decoder", new ProtobufDecoder(
						MessageBuf.Message.getDefaultInstance()));
				// pipeline.addLast("idleState", new IdleStateHandler(
				// OptionKey.READ_IDLE_TIME.intValue(),
				// OptionKey.WRITE_IDLE_TIME.intValue(), 0));// 心跳控制
				pipeline.addLast("handler", initHandler(dispatcher));
			};
		};
	}

	public abstract ThreadPoolExecutor createServcieExecutor();

	@Override
	public ConcurrentHashMap<SocketAddress, IEasyChannel> channels() {
		return channels;
	}

	@Override
	public EasyContext context() {
		return context;
	}

	@Override
	public ThreadPoolExecutor serviceExecutor() {
		return serviceExecutor;
	}

	@Override
	public void setOption(OptionKey key, Object value) {
		key.setValue(value);
	}
	@Override
	public Object getOption(OptionKey key){
		return key.value();
	}
	
	@Override
	public void shutdown() {
		if(workerGroup!=null){
			workerGroup.shutdownGracefully();
		}
		if(null !=serviceExecutor){
			serviceExecutor.shutdown();
		}
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
