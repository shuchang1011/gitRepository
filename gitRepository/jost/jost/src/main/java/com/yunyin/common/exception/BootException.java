package com.yunyin.common.exception;

public class BootException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BootException(String message){
		super(message);
	}
	
	public BootException(Throwable cause)
	{
		super(cause);
	}
	
	public BootException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
