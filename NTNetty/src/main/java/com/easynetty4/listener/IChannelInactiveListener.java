package com.easynetty4.listener;

import com.easynetty4.channel.IEasyChannel;
/**
 * 
 * 通道失效后侦听类
 *
 */
public interface IChannelInactiveListener {

	void channelInactive(IEasyChannel easyChannel);
}
