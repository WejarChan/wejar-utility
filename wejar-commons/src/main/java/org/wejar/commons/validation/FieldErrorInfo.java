package org.wejar.commons.validation;

import java.io.Serializable;

public class FieldErrorInfo implements Serializable{

	private static final long serialVersionUID = -2914423152662179071L;
	private String field;
	private String message;
    
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    
    
}
