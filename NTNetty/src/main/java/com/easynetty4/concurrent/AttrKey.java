package com.easynetty4.concurrent;


public class AttrKey<T> {

	private String key;
	private AttrKey(String key){
		this.key= key;
	}
	@Override
	public String toString() {
		return this.key;
	}
	public static <T>  AttrKey<T> valueOf(String value){
		AttrKey<T> attrKey = new AttrKey<T>(value);
		return attrKey;
	}
	public static void main(String[] args) {
		AttrKey<Integer> k1 = AttrKey.valueOf("aaaaaa");
		System.out.println(k1);
		AttrKey<Integer> k2 = AttrKey.valueOf("aaaaaa");
		System.out.println(k2);
		System.out.println(k1.toString().equals(k2.toString()));
	}
}
