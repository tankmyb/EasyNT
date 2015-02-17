package com.easy.ntconnector.service;

import org.springframework.stereotype.Service;

import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;

@Service
public class GcService implements IService{

	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		System.out.println("==============go gc======start=====");
		System.gc();
		System.out.println("==============go gc======end=====");
	}

}
