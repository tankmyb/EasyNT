package com.easy.ntconnector.service;

import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.easy.ntconnector.NTConnectorMain;
import com.easy.ntconnector.config.Common;
import com.easy.ntdb.dao.IUserDAO;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatReq;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;
import com.easynetty4.utils.PrintUtil;
import com.easynetty4.utils.ProtocolUtil;

@Service
public class ChatService implements IService{

	@Resource
	private IUserDAO userDAO;
	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		ChatReq req =ChatReq.parseFrom(message.getBody());
		//PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==1");
		String userNoKey = Common.USERNO_PERFIX+req.getFriendNo();
	    SocketAddress address= (SocketAddress)easyChannel.bootstrap().context().get(userNoKey);
	    if(address !=null){
	    	//PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==3");
	    	ChatResp.Builder resp=ChatResp.newBuilder();
	    	resp.setContent(req.getContent());
	    	resp.setFriendNo(req.getUserNo());
	    	
	    	Message respMessage = ProtocolUtil.builderHandleMessage(message.getHeader(),null, resp.build().toByteString());
	    	easyChannel.bootstrap().channels().get(address).write(respMessage);
	    }else {
	    	//PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==4");
	    	ChatReq.Builder chat = ChatReq.newBuilder();
			chat.setContent(req.getContent());
			chat.setFriendNo(req.getFriendNo());
			chat.setUserNo(req.getUserNo());
			Message m = ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.SINGLE_CHAT, chat.build().toByteString());
			IEasyNettyConnector client = (IEasyNettyConnector)easyChannel.bootstrap().context().get("connector");
			//System.out.println(client+"===");
			client.write(new InetSocketAddress("127.0.0.1",NTConnectorMain.handlerPort), m);
	    }
	}
}
