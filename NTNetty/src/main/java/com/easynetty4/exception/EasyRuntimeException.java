package com.easynetty4.exception;

public class EasyRuntimeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EasyRuntimeException(Throwable t){
		super(t);
	}
	public EasyRuntimeException(String message){
		super(message);
	}
}
