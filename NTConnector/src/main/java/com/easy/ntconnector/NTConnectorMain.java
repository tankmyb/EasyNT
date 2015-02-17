package com.easy.ntconnector;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easy.ntconnector.config.Common;
import com.easy.ntconnector.service.ChatRespService;
import com.easy.ntconnector.service.ChatService;
import com.easy.ntconnector.service.ErlangService;
import com.easy.ntconnector.service.GcService;
import com.easy.ntconnector.service.LoginConnectService;
import com.easy.ntconnector.service.LoginSyncService;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easynetty4.bootstrap.IEasyNettyAcceptor;
import com.easynetty4.bootstrap.tcp.acceptor.EasyNettyAcceptor;
import com.easynetty4.service.IService;
import com.easynetty4.service.ServiceManager;

public class NTConnectorMain {

	public static int handlerPort=10899;
	static String connectorKey="c1";
	static int port =10800;
	public static void main(String[] args) {
		if(args.length==3){
			handlerPort = Integer.parseInt(args[0]);
			connectorKey = args[1];
			port = Integer.parseInt(args[2]);
		}else if(args.length==2){
			connectorKey=args[0];
			port = Integer.parseInt(args[1]);
		}else if(args.length==1){
			connectorKey=args[0];
			if(connectorKey.equals("c1")){
				port =10800;
			}else {
				port =10801;
			}
		}
		System.out.println("===================");
		ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		IService loginConnectService = (LoginConnectService)context.getBean("loginConnectService");
		IService loginSyncService = (LoginSyncService)context.getBean("loginSyncService");
		IService chatService = (ChatService)context.getBean("chatService");
		IService chatRespService = (ChatRespService)context.getBean("chatRespService");
		IService erlangService = (ErlangService)context.getBean("erlangService");
		IService gcService = (GcService)context.getBean("gcService");
		
		ServiceManager.put(ServiceTypeEnum.CLIENT_CONNECT_LOGINER.value(), loginConnectService);
		ServiceManager.put(ServiceTypeEnum.LOGIN_SYNC.value(), loginSyncService);
		ServiceManager.put(ServiceTypeEnum.SINGLE_CHAT.value(), chatService);
		ServiceManager.put(ServiceTypeEnum.SINGLE_CHAT_RESP.value(), chatRespService);
		ServiceManager.put(ServiceTypeEnum.ERLANG_TEST.value(), erlangService);
		ServiceManager.put(ServiceTypeEnum.GO_GC.value(), gcService);
		IEasyNettyAcceptor acceptor = new EasyNettyAcceptor();
		acceptor.context().put(Common.CONNECTOR_NAME_KEY,connectorKey);
		
		/*SocketAddress address = new InetSocketAddress("127.0.0.1", handlerPort);
		IEasyNettyConnector client = new EasyNettyConnector();
		client.context().put("acceptor",acceptor);
		acceptor.context().put("connector", client);
		
		//System.out.println(client);
		client.addChannelActiveListener(new IChannelActiveListener() {
			@Override
			public void channelActive(final IEasyChannel channel) {
				ConnectNTHanderReq.Builder req = ConnectNTHanderReq.newBuilder();
				req.setNo(connectorKey);
				Message respMessage = ProtocolUtil.builderNewReqRespMessage(ServiceTypeEnum.CONNECTOR_CONNECT_HANDLERER, req.build().toByteString());
				channel.write(respMessage);
			}
		});
		client.connect(address);*/
		acceptor.bind(port);
	}
}
