package com.easy.ntclient;

import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.easy.ntclient.service.ChatRespService;
import com.easy.ntclient.ssl.SSLHttpClient;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.json.LoginResp;
import com.easy.ntprotocol.protobuf.ChatBuf.ChatReq;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginConnReq;
import com.easy.ntprotocol.protobuf.LoginBuf.LoginConnResp;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.bootstrap.tcp.connector.EasyNettyConnector;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.service.ServiceManager;
import com.easynetty4.utils.JacksonUtil;
import com.easynetty4.utils.ProtocolUtil;
import com.google.protobuf.InvalidProtocolBufferException;

public class NTClientMain {

	static String userNo="2";
	static int loginerPort = 10888;
	public static void main(String[] args) throws InterruptedException, IOException {
		if(args.length==2){
			userNo=args[0];
			loginerPort=Integer.parseInt(args[1]);
		}else if(args.length==1){
			userNo=args[0];
		}
		final AttributeKey<String> userKey = AttributeKey.valueOf("userKey");
		SSLHttpClient sslclient = new SSLHttpClient();
		String ret = sslclient.get("https://127.0.0.1:"+loginerPort+"/login?userNo="+userNo+"&userPwd=123");
		System.out.println(ret+"==="+userNo);
		final LoginResp resp = JacksonUtil.resolve(ret, LoginResp.class);
		
		if(resp.getRet()==1){
			final SocketAddress address = new InetSocketAddress(resp.getIp(), resp.getPort());
			final IEasyNettyConnector client = new EasyNettyConnector();
			ServiceManager.put(ServiceTypeEnum.SINGLE_CHAT.value(), new ChatRespService());
			client.addChannelActiveListener(new IChannelActiveListener() {
				@Override
				public void channelActive(final IEasyChannel channel) {
							LoginConnReq.Builder req = LoginConnReq.newBuilder();
							req.setUserNo(userNo);	
							req.setUserKey(resp.getUserKey());
							System.out.println(resp.getUserKey()+"======key");
							Message message =ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.CLIENT_CONNECT_LOGINER, req.build().toByteString());
							Message respMessage = (Message)channel.writeSync(message);
							LoginConnResp connResp=null;
							try {
								connResp = LoginConnResp.parseFrom(respMessage.getBody());
								System.out.println(connResp.getRet()+"===22=====ret");
								if(connResp.getRet()==-1){
									//client.getEasyChannel(address).close();
								    System.out.println("============shut");
									client.shutdown();
								}else {
									client.context().put("userNo",userNo);
									client.context().attr(userKey).set(resp.getUserKey());
								}
							} catch (InvalidProtocolBufferException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					
			});
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
							chat.setContent(arr[1]);
							chat.setFriendNo(arr[0]);
							chat.setUserNo(userNo);
							Message m = ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.SINGLE_CHAT, chat.build().toByteString());
							client.write(address, m);
						}
			
		}
		
	}
}
