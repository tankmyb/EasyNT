package com.easy.nthandler.service;

import java.net.SocketAddress;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.easy.ntdb.dao.IUserDAO;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatReq;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;
import com.easynetty4.utils.PrintUtil;
import com.easynetty4.utils.ProtocolUtil;

@Service
public class SingleChatService implements IService{

	@Resource
	private IUserDAO userDAO;
	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {

		ChatReq req =ChatReq.parseFrom(message.getBody());
		String friendNo= req.getFriendNo();
		String connecterKey = userDAO.getLogin(friendNo);
		if(StringUtils.isNotBlank(connecterKey)){
		    SocketAddress address= (SocketAddress)easyChannel.bootstrap().context().get(connecterKey);
		    //System.out.println(req.getFriendNo()+"===getFriendNo===="+address);
		    PrintUtil.println(req.getUserNo()+","+req.getFriendNo()+"==5=="+address);
		    if(address !=null){
		    	ChatReq.Builder resp=ChatReq.newBuilder();
		    	resp.setContent(req.getContent());
		    	resp.setFriendNo(friendNo);
		    	resp.setUserNo(req.getUserNo());
		    	Message respMessage = ProtocolUtil.builderHandleMessage(message.getHeader(),ServiceTypeEnum.SINGLE_CHAT_RESP, resp.build().toByteString());
		    	easyChannel.bootstrap().channels().get(address).write(respMessage);
		    }
		}
		
	    
		
	
		
	}

}
