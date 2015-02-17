package com.easynetty4.netty;

import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultAttributeMapTest {
	static DefaultAttributeMap map = new DefaultAttributeMap();
	static void put(int i){
		AttributeKey<Integer> KEY_USER_ID = AttributeKey.valueOf("userId"+i);
		map.attr(KEY_USER_ID).getAndSet(i);
	}
	static class Handler implements Runnable{
        private CountDownLatch end;
        private int i;
        public Handler(CountDownLatch end,int i){
        	this.end = end;
        	this.i=i;
        }
		@Override
		public void run() {
			
			put(i);
			end.countDown();
		}
    	
    }
	public static void main(String[] args) throws InterruptedException {
		Thread.sleep(10000);
		long startTime=System.currentTimeMillis();
		int size = 500000;
    	ExecutorService threadPool = Executors.newCachedThreadPool();
        CountDownLatch end = new CountDownLatch(size);
        for(int i=0;i<size;i++){
        	threadPool.execute(new Handler(end,i));
        }
        System.out.println("start");
        end.await();
        System.out.println("end======="+(System.currentTimeMillis()-startTime));
        AttributeKey<Integer> KEY_USER_ID = AttributeKey.valueOf("userId500");
		System.out.println(map.attr(KEY_USER_ID).get());
        threadPool.shutdown();
        Thread.sleep(100000);
		
	}
}
