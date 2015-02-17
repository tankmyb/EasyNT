package com.easy.ntclient.service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.ntprotocol.protobuf.ChatBuf.ChatResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;
import com.easynetty4.utils.DateTimeUtil;

public class MulitChatRespService implements IService{
	static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		//System.out.println("=============MulitChatRespService===============");
		ChatResp resp = ChatResp.parseFrom(message.getBody());
		String userNo=(String)easyChannel.bootstrap().context().get("userNo");
		StringBuffer sb = new StringBuffer();
		sb.append("1,").append(userNo).append(",").append(resp.getFriendNo()).append(",");
		sb.append(resp.getContent()).append(",").append(System.currentTimeMillis());
		logger.error(sb.toString());
		/*Integer v= map.get(userNo);
		if(v==null){
			map.put(userNo, 1);
		}else {
			if(v==3){
				easyChannel.bootstrap().shutdown();
			}else {
				map.put(userNo, v+1);
			}
		}*/
		
	}

}
