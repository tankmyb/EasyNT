package com.easynetty4.listener;

import com.easynetty4.channel.IEasyChannel;

/**
 * 
 * 通道建立成功后侦听类
 *
 */
public interface IChannelActiveListener {

	void channelActive(IEasyChannel channel);
}
