package com.easynetty4.service;

import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.channel.IEasyChannel;

public interface IService {

	void handle(MessageBuf.Message message,IEasyChannel easyChannel) throws Exception;
}
