package com.easy.ntprotocol;

public enum ServiceTypeEnum {

	LOGIN_SYNC(11),
	CLIENT_CONNECT_LOGINER(12),
	SINGLE_CHAT(13),
	CONNECTOR_CONNECT_HANDLERER(14),
	SINGLE_CHAT_RESP(15),
	ERLANG_TEST(16),
	GO_GC(17);
	private int value;
	private ServiceTypeEnum(int value){
		this.value =value;
	}
	public int value(){
		return value;
	}
}
