package com.easynetty4.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServiceManager {

	public static Map<Integer,IService> map = new HashMap<Integer,IService>();
	public static void put(Integer key,IService value){
		map.put(key, value);
	}
	public static IService get(Integer key){
		return map.get(key);
	}
	public static Iterator<IService> iterator(){
		return map.values().iterator();
	}
}
