package com.easy.nthandler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easy.nthandler.service.ConnectService;
import com.easy.nthandler.service.SingleChatService;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easynetty4.bootstrap.IEasyNettyAcceptor;
import com.easynetty4.bootstrap.tcp.acceptor.EasyNettyAcceptor;
import com.easynetty4.service.IService;
import com.easynetty4.service.ServiceManager;

public class NTHandlerMain {

	public static void main(String[] args) {
		int port = 10899;
		if(args.length>0){
			port=Integer.parseInt(args[0]);
		}
		ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		IService connectService = (ConnectService)context.getBean("connectService");
		IService singleChatService = (SingleChatService)context.getBean("singleChatService");
		ServiceManager.put(ServiceTypeEnum.SINGLE_CHAT.value(), singleChatService);
		ServiceManager.put(ServiceTypeEnum.CONNECTOR_CONNECT_HANDLERER.value(), connectService);
		IEasyNettyAcceptor loginer = new EasyNettyAcceptor();
		loginer.bind(port);
	}
}
