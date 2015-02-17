package com.easy.nthandler.service;

import org.springframework.stereotype.Service;

import com.easy.ntprotocol.protobuf.ConnectNTHanderBuf.ConnectNTHanderReq;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.listener.IChannelInactiveListener;
import com.easynetty4.service.IService;

@Service
public class ConnectService implements IService{

	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		 ConnectNTHanderReq req = ConnectNTHanderReq.parseFrom(message.getBody());
		 final String no = req.getNo();
		//System.out.println(no);
		//在上下文增加远程地址
		easyChannel.bootstrap().context().put(no,easyChannel.channel().remoteAddress());
		req=null;
		easyChannel.bootstrap().addChannelInactiveListener(new IChannelInactiveListener() {
			@Override
			public void channelInactive(IEasyChannel easyChannel) {
				//System.out.println("===="+easyChannel.bootstrap().context().get(no)+"=====1");
				//System.out.println("======channelInactive======"+no);
				easyChannel.bootstrap().context().remove(no);
				//System.out.println(easyChannel.bootstrap().context().get(no)+"=-====2");
			}
		});
	}

}
