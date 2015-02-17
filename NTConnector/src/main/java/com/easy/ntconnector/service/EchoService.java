package com.easy.ntconnector.service;

import org.springframework.stereotype.Service;

import com.easy.ntprotocol.protobuf.EchoBuf.Echo;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;

@Service
public class EchoService implements IService{

	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		Echo echo = Echo.parseFrom(message.getBody());
		
	}

}
