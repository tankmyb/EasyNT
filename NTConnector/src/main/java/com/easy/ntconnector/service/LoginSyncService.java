package com.easy.ntconnector.service;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.easy.ntconnector.config.Common;
import com.easy.ntconnector.util.TimerScheduler;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginSyncReq;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginSyncResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;
import com.easynetty4.utils.PrintUtil;
import com.easynetty4.utils.ProtocolUtil;

@Service
public class LoginSyncService implements IService {

	@Override
	public void handle(Message message, final IEasyChannel easyChannel) throws Exception {
		final LoginSyncReq req = LoginSyncReq.parseFrom(message.getBody());
		final String userKey=Common.USERKEY_PERFIX+req.getUserKey();
		//PrintUtil.println(req.getUserKey()+"========req.getUserKey()=="+req.getUsrNo());
		easyChannel.bootstrap().context().put(userKey, req.getUsrNo());
		LoginSyncResp.Builder resp = LoginSyncResp.newBuilder();
		resp.setRet(1);
		easyChannel.write(ProtocolUtil.builderMessage(message.getHeader(), resp
				.build().toByteString()));
		TimerScheduler.schedule(userKey,new TimerTask() {
            public void run(Timeout timeout) throws Exception {
            	if(null == easyChannel.bootstrap().context().get(Common.USERNO_PERFIX+req.getUsrNo())){
            		easyChannel.bootstrap().context().remove(userKey);
            	}
            	TimerScheduler.remove(userKey);
            	//System.out.println("=====schedule====");
            }
        }, 10,TimeUnit.SECONDS);
	}

}
