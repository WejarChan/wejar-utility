package org.wejar.commons.exception;

public class NoLoginException extends BusinessException {
	public static final String CODE = "NO_LOGIN_USER";
	public static final String MESSAGE = "没有登录用户";
	
	public NoLoginException(){
		super();
		setCode(CODE);
		setMessage(MESSAGE);
	}
}
