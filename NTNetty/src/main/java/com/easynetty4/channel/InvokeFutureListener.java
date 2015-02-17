package com.easynetty4.channel;


public interface InvokeFutureListener {
	void operationComplete(InvokeFuture future) throws Exception;
}
