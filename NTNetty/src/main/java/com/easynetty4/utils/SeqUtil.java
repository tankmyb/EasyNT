package com.easynetty4.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class SeqUtil {
	public static final int MAXVALUE = Integer.MAX_VALUE-10000000;
	 public static AtomicInteger seq = new AtomicInteger();
	  public static int incrementSeq(){
	  	int s =seq.incrementAndGet();
			if(s>=MAXVALUE){
				seq.set(0);
			}
	  	return s;
	  }
}
