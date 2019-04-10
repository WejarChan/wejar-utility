package org.wejar.commons.exception;

import java.util.List;

import org.wejar.commons.validation.FieldErrorInfo;


public class ParamValidException extends Throwable{
	
    private List<FieldErrorInfo> fieldErrors;
    
    private boolean useRespBody;
    
    
    public ParamValidException(List<FieldErrorInfo> errors,boolean useRespBody) {
		super();
    	this.useRespBody = useRespBody;
        this.fieldErrors = errors;
    }

    
	public List<FieldErrorInfo> getFieldErrors() {
		return fieldErrors;
	}

	public boolean isUseRespBody() {
		return useRespBody;
	}
    
}
