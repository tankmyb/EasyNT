package com.easynetty4.bean.proto;

import org.junit.Test;

public class HeaderBufTest {

	@Test
	public void test() {
		HeaderBuf.Header.Builder h = HeaderBuf.Header.newBuilder();
		h.setStype(1221111);
		//h.setMtype(MessageType.AUTH_REQ);
		System.out.println(h.build().toByteArray().length);
	}

}
