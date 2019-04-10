package org.wejar.commons.exception;

public class ParamsConstraintViolationException extends BusinessException {

	public static final String CODE = "PARAMS_CONSTRAINT_VIOLATION";
	public static final String MESSAGE = "参数校验不通过";
	
	public ParamsConstraintViolationException(){
		super();
		setCode(CODE);
		setMessage(MESSAGE);
	}
}
