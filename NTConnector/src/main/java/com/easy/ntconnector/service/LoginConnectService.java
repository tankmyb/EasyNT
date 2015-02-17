package com.easy.ntconnector.service;

import java.net.SocketAddress;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.netty.util.AttributeKey;

import com.easy.ntconnector.config.Common;
import com.easy.ntconnector.util.TimerScheduler;
import com.easy.ntdb.dao.IUserDAO;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginConnReq;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginConnResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.listener.IChannelInactiveListener;
import com.easynetty4.service.IService;
import com.easynetty4.utils.PrintUtil;
import com.easynetty4.utils.ProtocolUtil;

@Service
public class LoginConnectService implements IService{
	@Resource
	private IUserDAO userDAO;
	@Override
	public void handle(Message message, IEasyChannel easyChannel) throws Exception {
		LoginConnReq req = LoginConnReq.parseFrom(message.getBody());
		final String userKey = Common.USERKEY_PERFIX+req.getUserKey();
		
		//System.out.println(req.getUserKey()+"==="+req.getUserNo());
		//System.out.println(easyChannel.bootstrap().context().get(userKey));
		
		LoginConnResp.Builder resp = LoginConnResp.newBuilder();
		//PrintUtil.println(req.getUserNo()+"=2=="+(String)easyChannel.bootstrap().context().get(Common.USERKEY_PERFIX+req.getUserKey()));
		TimerScheduler.cancel(userKey);
		if(req.getUserNo().equals((String)easyChannel.bootstrap().context().get(userKey))){
			AttributeKey<String> key = AttributeKey.valueOf(Common.CONNECTOR_NAME_KEY);
			//System.out.println(userDAO+"=="+req.getUserNo());
			userDAO.saveLogin(req.getUserNo(), easyChannel.bootstrap().context().attr(key).get());
			final String userNoKey = Common.USERNO_PERFIX+req.getUserNo();
			easyChannel.bootstrap().context().put(userNoKey,easyChannel.channel().remoteAddress());
			easyChannel.channel().attr(AttributeKey.valueOf("userNo")).set(req.getUserNo());
			easyChannel.addChannelInactiveListener(new IChannelInactiveListener() {
				@Override
				public void channelInactive(IEasyChannel easyChannel) {
					//PrintUtil.println(userKey+"===========remove==="+userNoKey);
					easyChannel.bootstrap().context().remove(userNoKey);
					//System.out.println(easyChannel.bootstrap().context().get(userKey)+"===========1");
					easyChannel.bootstrap().context().remove(userKey);
					//System.out.println(easyChannel.bootstrap().context().get(userKey)+"=============2");
				}
			});
			resp.setRet(1);
		}else {
			resp.setRet(-1);
		}
		//System.out.println(easyChannel.channel().id()+"=======channels===");
		easyChannel.write(ProtocolUtil.builderMessage(message.getHeader(), resp.build().toByteString()));
		
		
	}

}
