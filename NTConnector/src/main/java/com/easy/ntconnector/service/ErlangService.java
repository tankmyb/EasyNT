package com.easy.ntconnector.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.easy.ntprotocol.protobuf.EchoBuf;
import com.easy.ntprotocol.protobuf.EchoBuf.Echo;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.service.IService;

@Service
public class ErlangService implements IService{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	AtomicInteger ai = new AtomicInteger();
	@Override
	public void handle(Message message, IEasyChannel easyChannel)
			throws Exception {
		//System.out.println("sdserver:" + "channelRead:" + message.getHeader().getMtype());
		//System.out.println(message.getBody());
        //Echo h = Echo.parseFrom(message.getBody());
        //System.out.println(h.getContent()+"================"+h.getValue());
		// long size = carInfoReq.getSerializedSize();
		// byte[] buf = carInfoReq.toByteArray();
        Message.Builder m = Message.newBuilder();
        m.setHeader(message.getHeader());
        
        Echo.Builder e= EchoBuf.Echo.newBuilder();
        e.setContent("dssds极从");
        e.setValue(58);
        m.setBody(e.build().toByteString());
        easyChannel.write(m.build());
        logger.error(ai.incrementAndGet()+"");
	}

}
