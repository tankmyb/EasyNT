package com.easynetty4.dispatcher;

import java.util.List;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.listener.IChannelInactiveListener;


public interface INettyDispatcher {

	/**
	 * 根据不同的业务类型分发消息
	 * @param message
	 * @param channel
	 */
	 void dispatchMessageReceived(MessageBuf.Message message,IEasyChannel channel);
	 /**
	  * 通道建立后自定义资源加载
	  * @param activeListeners
	  */
	 void handleChannelActive(IEasyChannel channel);
	 
}
