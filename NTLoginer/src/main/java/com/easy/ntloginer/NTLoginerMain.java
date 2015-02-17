package com.easy.ntloginer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.easy.ntdb.dao.IUserDAO;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.bootstrap.tcp.connector.EasyNettyConnector;
import com.easynetty4.config.optionkey.OptionKey;
import com.easynetty4.threadpool.AbortPolicyWithReport;
import com.easynetty4.threadpool.NamedThreadFactory;

public class NTLoginerMain {
	//192.168.64.173
	//192.168.65.22
    public static String ip = "192.168.65.22";
	public static int connectorPort1=10800;
	public static int connectorPort2=10801;
	static int loginerPort=10888;
	public static void main(String[] args) throws Exception {
		if(args.length==3){
			connectorPort1=Integer.parseInt(args[0]);
			connectorPort2=Integer.parseInt(args[1]);
			loginerPort=Integer.parseInt(args[2]);
		}
		ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
		//System.out.println(context+"==========context");
		 IUserDAO userDAO= (IUserDAO)context.getBean("userDAO");
		SocketAddress address = new InetSocketAddress("127.0.0.1", connectorPort1);
		//SocketAddress address1 = new InetSocketAddress("127.0.0.1", connectorPort2);
		IEasyNettyConnector client = new EasyNettyConnector();
		//client.connect(address,address1);
		client.connect(address);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				20,
				20, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(OptionKey.POOLQUEUECAPACITY
						.intValue()), new NamedThreadFactory("serviceExecutor",
						true), new AbortPolicyWithReport("serviceExecutor"));
		NTLoginerServer loginer = new NTLoginerServer(loginerPort,client,userDAO,threadPoolExecutor);
		loginer.run();
	}
}
