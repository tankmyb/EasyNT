package com.easy.ntconnector.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		ChatService service = (ChatService)context.getBean("chatService");
		service.handle(null, null);
	}
}
