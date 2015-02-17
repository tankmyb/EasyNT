package com.easy.ntconnector.service;

import io.netty.util.AttributeKey;

import java.net.SocketAddress;

import org.springframework.stereotype.Service;

import com.easy.ntconnector.config.Common;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatReq;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyNettyAcceptor;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;
import com.easynetty4.utils.ProtocolUtil;

@Service
public class ChatRespService implements IService{
	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {

        //System.out.println("======handler==============ChatRespService");
		ChatReq req =ChatReq.parseFrom(message.getBody());
		//System.out.println(req.getFriendNo()+"=="+req.getContent());
		AttributeKey<IEasyNettyAcceptor> acceptorKey = AttributeKey.valueOf("acceptor");
		IEasyNettyAcceptor acceptor = easyChannel.bootstrap().context().attr(acceptorKey).get();
		AttributeKey<SocketAddress> userNoKey=AttributeKey.valueOf(Common.USERNO_PERFIX+req.getFriendNo());
	    SocketAddress address= acceptor.context().attr(userNoKey).get();
	    //System.out.println(req.getFriendNo()+"===getFriendNo===="+address);
	    if(address !=null){
	    	ChatResp.Builder resp=ChatResp.newBuilder();
	    	resp.setContent(req.getContent());
	    	resp.setFriendNo(req.getUserNo());
	    	//PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==2");
	    	Message respMessage = ProtocolUtil.builderHandleMessage(message.getHeader(),ServiceTypeEnum.SINGLE_CHAT, resp.build().toByteString());
	    	acceptor.channels().get(address).write(respMessage);
	    }else {
	    	//PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==7");
	    }
		
	}

}
