package org.wejar.commons.exception;

public class NoPermissionException extends BusinessException {
	public static final String CODE = "NO_PERMISSION";
	public static final String MESSAGE = "没有操作权限";
	
	public NoPermissionException(){
		super();
		setCode(CODE);
		setMessage(MESSAGE);
	}
}
