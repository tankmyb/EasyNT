package com.easynetty4.config.optionkey;

import java.util.HashMap;
import java.util.Map;


public enum OptionKey {

	/**
	 * 写空闲时间(秒), default is 30.
	 */
	WRITE_IDLE_TIME(30),
	/**
	 * 读空闲时间(秒), default is 60.
	 */
	READ_IDLE_TIME(60),
	/**
	 * 是否需要业务线程池,默认为true（服务端）
	 */
	IS_NEED_SERVICE_THREADPOOL(true),
	/**
	 * 是否重连,默认为true(客户端)
	 */
	IS_RECONN(true),

	/**
	 * 连接超时(毫秒),默认为3000(客户端)
	 */
	CONNECT_TIMEOUT_MILLIS(3000),
	/**
	 * boss线程池,默认为1(服务端)
	 */
	BOSSGROUP(1),
	/**
	 * worker线程池,默认为0
	 */
	WORKERGROUP(0),
	/**
	 * 业务线程池最小线程数,默认为1
	 */
	MINIMUMPOOLSIZE(1),
	/**
	 * 业务线程池最大线程数,默认为150
	 */
	MAXIMUMPOOLSIZE(10),
	/**
	 * 业务线程池队列最大容量,默认为10000
	 */
	POOLQUEUECAPACITY(10000),
	/**
	 * 是否需要定时器,默认为true
	 */
	NEED_TIMER(true),
	/**
	 * 定时器线程数,默认为1
	 */
	TIMER_POOLSIZE(1);
	private Object value;
	private OptionKey(Object value) {
		this.value=value;
	}
    public int intValue(){
    	return (Integer)value;
    }
    public boolean booleanValue(){
    	return (Boolean)value;
    }
    public String stringValue(){
    	return (String)value;
    }
    public Object value(){
    	return value;
    }
    public void setValue(Object value){
    	this.value = value;
    }
    
    public static Map<OptionKey, Object> toMap(){
    	Map<OptionKey, Object> map = new HashMap<OptionKey, Object>();
    	for(OptionKey option:values()){
    		map.put(option, option.value);
    	}
    	return map;
    }
    public static void main(String[] args) {
    	CONNECT_TIMEOUT_MILLIS.setValue(7000);
    	System.out.println(CONNECT_TIMEOUT_MILLIS.intValue());
	}
}