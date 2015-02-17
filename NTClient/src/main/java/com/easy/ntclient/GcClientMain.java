package com.easy.ntclient;

import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatReq;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.bootstrap.tcp.connector.EasyNettyConnector;
import com.easynetty4.utils.ProtocolUtil;

public class GcClientMain {

	public static void main(String[] args) throws InterruptedException, IOException {
		    String ip="127.0.0.1";
			final SocketAddress address = new InetSocketAddress(ip, 10800);
			final IEasyNettyConnector client = new EasyNettyConnector();
			ChannelFuture[] futures = client.connect(address);
			if(futures.length==0){
				client.shutdown();
				return;
			}
			// 控制台输入
						BufferedReader in = new BufferedReader(new InputStreamReader(
								System.in));
						for (;;) {
							String line = in.readLine();
							if (line == null || "".equals(line)) {
								continue;
							}
							if(line.equals("bye")){
								client.shutdown();
							}
							String[] arr = line.split(",");
							ChatReq.Builder chat = ChatReq.newBuilder();
							chat.setContent("aaaaaa");
							chat.setFriendNo("");
							chat.setUserNo("");
							Message m = ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.GO_GC, chat.build().toByteString());
							client.write(address, m);
						}
			
		
	}
}
