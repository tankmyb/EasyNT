package com.easynetty4.config.optionkey;

import java.util.HashMap;
import java.util.Map;

public class Options {

	private static Map<OptionKey,Object> options = new HashMap<OptionKey,Object>();
	static{
		options.putAll(OptionKey.toMap());
	}
	public static Object get(OptionKey key){
			return options.get(key);
	}
	public static void set(OptionKey key,Object value){
		if(value==null){
			throw new RuntimeException("value is not null");
		}
		options.put(key, value);
	}
	public static void main(String[] args) {
		//Options.setOption(OptionKey.WRITE_IDLE_TIME, 100);
		System.out.println(Options.get(OptionKey.WRITE_IDLE_TIME));
	}
}
