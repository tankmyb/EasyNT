package com.easynetty4.bootstrap.tcp.connector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.AEasyBootstrap;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.bootstrap.tcp.EasyNettyConnectorHandler;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.channel.InvokeFuture;
import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.dispatcher.INettyDispatcher;
import com.easynetty4.listener.IChannelInactiveListener;
import com.easynetty4.threadpool.AbortPolicyWithReport;
import com.easynetty4.threadpool.NamedThreadFactory;

public abstract class AEasyNettyConnector extends AEasyBootstrap implements
		IEasyNettyConnector {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected Bootstrap bootstrap;
	//不要设成null，因为init执行时间比变量早，如果设成null,相当于将其重新设置
	protected ScheduledExecutorService reconnectTimer;
	protected ReentrantLock reconnLock = new ReentrantLock();
	private ConcurrentHashMap<SocketAddress, AtomicInteger> disconnAddressList = new ConcurrentHashMap<SocketAddress, AtomicInteger>();
    private AtomicBoolean isShutdown = new AtomicBoolean(false);
	public AEasyNettyConnector() {
		super();
	}

	public AEasyNettyConnector(int workerThreadCount) {
		super(workerThreadCount);
	}

	public AEasyNettyConnector(int workerThreadCount, int serviceCount) {
		super(workerThreadCount, serviceCount);
	}
	protected void init() {
		if(bootstrap==null){
			super.init();
			bootstrap = new Bootstrap();
			bootstrap.group(workerGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.handler(channelInitializer);
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
					OptionKey.CONNECT_TIMEOUT_MILLIS.intValue()).option(
					ChannelOption.TCP_NODELAY, true);
			if (OptionKey.IS_RECONN.booleanValue()) {
				reconnectTimer = createReconnectScheduler();
				addChannelInactiveListener(new IChannelInactiveListener() {
					@Override
					public void channelInactive(IEasyChannel easyChannel) {
						SocketAddress address = easyChannel.channel()
								.remoteAddress();
						if (null != address) {
							addDisconnectAddress(address);
							addScheduledReconnTask();
						}
					}
				});
			}
		}
		
	}

	protected ChannelFuture connectAddress(final SocketAddress address)
			throws InterruptedException {
		return bootstrap.connect(address).sync();
	}

	protected abstract void reconnect(final SocketAddress address);

	private ScheduledExecutorService createReconnectScheduler() {
		return Executors.newScheduledThreadPool(1, new NamedThreadFactory(
				"ReconnectScheduler", true));
	}

	protected void addScheduledReconnTask() {
		if (!isShutdown.get()) {
			reconnectTimer.schedule(new Runnable() {
				@Override
				public void run() {
					reconnLock.lock();
					try {
						Set<SocketAddress> addressSet = getDisconnectAddress();
						for (SocketAddress address : addressSet) {
							removeDisconnect(address);
							reconnect(address);
						}
					} finally {
						reconnLock.unlock();
					}
				}
			}, 2, TimeUnit.SECONDS);
		}
	}

	@Override
	public void shutdown() {
		isShutdown.set(true);
		super.shutdown();
		if (null != reconnectTimer) {
			reconnectTimer.shutdown();
		}
		bootstrap = null;
	}

	@Override
	public InvokeFuture writeAsync(SocketAddress address, Message message) {
		return channels.get(address).writeAsync(message);
	}

	@Override
	public Object writeSync(SocketAddress address, MessageBuf.Message message) {
		return channels.get(address).writeSync(message);
	}

	@Override
	public Object writeSync(SocketAddress address, MessageBuf.Message message,
			long timeout, TimeUnit unit) {
		return channels.get(address).writeSync(message, timeout, unit);
	}

	@Override
	public ChannelFuture write(SocketAddress address, MessageBuf.Message message) {
		return channels.get(address).write(message);
	}

	public Set<SocketAddress> getDisconnectAddress() {
		return disconnAddressList.keySet();
	}

	@Override
	public void addDisconnectAddress(SocketAddress address) {
		AtomicInteger num = disconnAddressList.putIfAbsent(address,
				new AtomicInteger(1));
		if (num != null) {
			num.getAndIncrement();
		}
	}

	public void removeDisconnect(SocketAddress address) {
		disconnAddressList.remove(address);
	}

	@Override
	public ThreadPoolExecutor createServcieExecutor() {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				OptionKey.MINIMUMPOOLSIZE.intValue(),
				OptionKey.MAXIMUMPOOLSIZE.intValue(), 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(OptionKey.POOLQUEUECAPACITY
						.intValue()), new NamedThreadFactory("serviceExecutor",
						true), new AbortPolicyWithReport("serviceExecutor"));
		return threadPoolExecutor;

	}

	@Override
	public ChannelHandlerAdapter initHandler(INettyDispatcher dispatcher) {
		return new EasyNettyConnectorHandler(dispatcher, this);
	}

	public abstract ChannelFuture[] connect(SocketAddress... addressArr);
}
