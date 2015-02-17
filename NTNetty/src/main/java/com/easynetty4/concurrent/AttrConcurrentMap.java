package com.easynetty4.concurrent;

import java.util.concurrent.ConcurrentHashMap;

public class AttrConcurrentMap {

	private ConcurrentHashMap<AttrKey<?>, AttrValue<?>> attrMap = new ConcurrentHashMap<AttrKey<?>, AttrValue<?>>();
	
	public AttrValue<?> attr(AttrKey<?> key){
		return attrMap.get(key);
	}
	public static void main(String[] args) {
		
	}
}
