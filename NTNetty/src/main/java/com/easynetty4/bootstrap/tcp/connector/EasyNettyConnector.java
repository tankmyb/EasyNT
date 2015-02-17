package com.easynetty4.bootstrap.tcp.connector;

import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;

import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.utils.StackTraceUtil;

public class EasyNettyConnector extends AEasyNettyConnector  {

	public EasyNettyConnector() {
		super();
	}

	public EasyNettyConnector(int workerThreadCount) {
		super(workerThreadCount);
	}
	protected void init(){
		setOption(OptionKey.READ_IDLE_TIME, 180);
		setOption(OptionKey.WRITE_IDLE_TIME, 60);
		setOption(OptionKey.MAXIMUMPOOLSIZE, 2);
		setOption(OptionKey.IS_RECONN, false);
		super.init();
	}
	@Override
	public ChannelFuture[] connect(SocketAddress... addressArr) {
		ChannelFuture[] futures = new ChannelFuture[addressArr.length];
		int i=0;
		for (SocketAddress address : addressArr) {
			try {
				futures[i++]=connectAddress(address);
				logger.info("connect to {} success", address);
			} catch (Exception e) {
				if (OptionKey.IS_RECONN.booleanValue()) {
					addDisconnectAddress(address);
					addScheduledReconnTask();
				}
				logger.error("connect to {} failed. error:{}", address,
						StackTraceUtil.getStackTrace(e));
			}
		}
		return futures;
	}

	@Override
	protected void reconnect(SocketAddress address) {
		connect(address);
		
	}


}
