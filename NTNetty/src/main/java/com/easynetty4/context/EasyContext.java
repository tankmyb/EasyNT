package com.easynetty4.context;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;

public class EasyContext {

	private  AttributeMap context = new DefaultAttributeMap();

	public <T> Attribute<T> attr(AttributeKey<T> key){
		return context.attr(key);
	}
	private AttributeKey<Object> valueOfKey(String key){
		return AttributeKey.valueOf(key);
	}
	public void put(String key,Object value){
		context.attr(valueOfKey(key)).set(value);
	}
	public Object get(String key){
		return context.attr(valueOfKey(key)).get();
	}
	public void remove(String key){
		context.attr(valueOfKey(key)).remove();
	}
}
