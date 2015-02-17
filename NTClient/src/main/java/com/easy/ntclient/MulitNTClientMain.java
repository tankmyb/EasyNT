package com.easy.ntclient;

import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easy.ntclient.bean.ClientBean;
import com.easy.ntclient.service.MulitChatRespService;
import com.easy.ntclient.ssl.SimpleHttpClient;
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

public class MulitNTClientMain {
	protected static final Logger logger = LoggerFactory.getLogger(MulitNTClientMain.class);
	private static Semaphore semaphore = null;
	static int loginerPort = 10888;
	//192.168.65.22
	//127.0.0.1
	static String ip="192.168.65.22";
	static class ClientLogin implements Runnable{
        private int index;
        public  ClientLogin(int index) {
        	this.index = index;
		}
		@Override
		public void run() {
	       final CountDownLatch end = new CountDownLatch(5);
	       final Map<String,ClientBean> map = new HashMap<String,ClientBean>();
			for(int i=1;i<=5;i++){
				final String userNo=(((index-1)*5)+i)+"";
				//System.out.println(userNo+"==========userNo");
				final AttributeKey<String> userKey = AttributeKey.valueOf("userKey");
				String ret = SimpleHttpClient.get("http://"+ip+":"+loginerPort+"/login?userNo="+userNo+"&userPwd=123");
				//System.out.println(ret+"==="+userNo);
				final LoginResp resp = JacksonUtil.resolve(ret, LoginResp.class);
				
				if(resp.getRet()==1){
					
					final SocketAddress address = new InetSocketAddress(ip, resp.getPort());
					final IEasyNettyConnector client = new EasyNettyConnector();
					//System.out.println(client.getOption(OptionKey.IS_RECONN)+"====IS_RECONN");
					ServiceManager.put(ServiceTypeEnum.SINGLE_CHAT.value(), new MulitChatRespService());
					client.addChannelActiveListener(new IChannelActiveListener() {
						@Override
						public void channelActive(final IEasyChannel channel) {
									LoginConnReq.Builder req = LoginConnReq.newBuilder();
									req.setUserNo(userNo);
									req.setUserKey(resp.getUserKey());
									//System.out.println(resp.getUserKey()+"======key");
									Message message =ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.CLIENT_CONNECT_LOGINER, req.build().toByteString());
									Message respMessage = (Message)channel.writeSync(message);
									LoginConnResp connResp=null;
									try {
										connResp = LoginConnResp.parseFrom(respMessage.getBody());
										//System.out.println(connResp.getRet()+"===22=====ret==="+userNo+"==="+resp.getUserKey());
										if(connResp.getRet()==-1){
											client.shutdown();
										}else {
											client.context().put("userNo",userNo);
											client.context().attr(userKey).set(resp.getUserKey());
											List<String> friends = resp.getFriends();
											//logger.error(userNo+"==="+friends.size());
											ClientBean bean = new ClientBean();
											bean.setFriends(friends);
											bean.setAddress(address);
											bean.setClient(client);
											map.put(userNo, bean);
											end.countDown();
											//System.out.println(end.getCount()+"======"+userNo);
											
											
										}
									} catch (InvalidProtocolBufferException e) {
										e.printStackTrace();
									}
								}
							
					});
					ChannelFuture[] futures = client.connect(address);
					if(futures.length==0){
						client.shutdown();
						return;
					}
				}
			}
			try {
				end.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Set<String> userSet = map.keySet();
			for(String u:userSet){
				ClientBean bean = map.get(u);
				List<String> friends = bean.getFriends();
				for(String f:friends){
					//System.out.println(f+"==============friend==="+userNo);
					ChatReq.Builder chat = ChatReq.newBuilder();
					chat.setContent("hello:"+f);
					chat.setFriendNo(f);
					chat.setUserNo(u);
					Message m = ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.SINGLE_CHAT, chat.build().toByteString());
					bean.getClient().write(bean.getAddress(), m);
				}
			}
			try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			for(String u:userSet){
				ClientBean bean = map.get(u);
				//System.out.println(bean.getAddress()+"===========1");
				bean.getClient().shutdown();
			}
			//System.out.println("=====semaphore.release()=======");
			semaphore.release();
		}
		
	}
	public static void main(String[] args) throws InterruptedException, IOException {
		
		//long startTime = System.currentTimeMillis();
		int size = 20;
		int s=50;
		if(args.length==2){
			size = Integer.parseInt(args[0]);
			s=Integer.parseInt(args[1]);
		}
		semaphore = new Semaphore(s);
    	ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for(int i=1;i<=size;i++){
        	threadPool.execute(new ClientLogin(i));
        	semaphore.acquire();
        	System.out.println("=1==========="+i);
        	/*if(i%20000==0){
        		i=1;
        	}*/
        }
        //System.out.println("start");
        //System.out.println("end====="+(System.currentTimeMillis()-startTime));
        //threadPool.shutdown();
	}
}
