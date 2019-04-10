package org.wejar.commons.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

public class ValidationUtils {
    
    /**
     * 使用hibernate的注解来进行验证
     * 
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
    public static ValidatorImpl validatorImpl = (ValidatorImpl) validator;
    
    public static LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    
    public static <T> List<FieldErrorInfo> validateParameters(T object, Method method, Object[] parameterValues, Class<?>... groups){
    	Set<ConstraintViolation<T>> constraintViolations = validatorImpl.validateParameters(object, method, parameterValues, groups);
    	
    	List<FieldErrorInfo> fieldErrors = new ArrayList<FieldErrorInfo>();
        // 抛出检验异常
        if (constraintViolations.size() > 0) {
        	Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
        	String[] paramNames = nameDiscoverer.getParameterNames(method);
        	while(it.hasNext()){
        		ConstraintViolation<T> constraintViolation = it.next();
        		
        		String field = constraintViolation.getPropertyPath().toString();
        		String message = constraintViolation.getMessage();
        		if(field != null && field.length() >0) {
        			if(field.matches("^"+method.getName()+".arg\\d+"+"$")) {
        				//login.arg0
        				//变量名没显示的情况下
        				String idx = field.substring(method.getName().length()+".arg".length());
        				int index = Integer.parseInt(idx);
        				field = paramNames[index];
        			}else {
        				//login.phone
        				field = field.split("\\.")[1];
        			}
        			
        		}
        		
        		FieldErrorInfo error = new FieldErrorInfo();
        		error.setField(field);
        		error.setMessage(message);
        		fieldErrors.add(error);
        	}
        }
        
        return fieldErrors;
    }
    
    /**
     * 功能描述: 根据hibernateValidation注解去验证一个参数实体
     *
     * @param obj	写有hibernateValidation注解的类
     * @return 
     */
    public static <T> List<FieldErrorInfo> validate(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        
        List<FieldErrorInfo> fieldErrors = new ArrayList<FieldErrorInfo>();
        // 抛出检验异常
        if (constraintViolations.size() > 0) {
        	Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
        	while(it.hasNext()){
        		ConstraintViolation<T> constraintViolation = it.next();
        		String field = constraintViolation.getPropertyPath().toString();
        		String message = constraintViolation.getMessage();
        		FieldErrorInfo error = new FieldErrorInfo();
        		error.setField(field);
        		error.setMessage(message);
        		fieldErrors.add(error);
        	}
        }
        return fieldErrors;
    }
    
    /**
     * 校验一组参数
     * @param args	参数数组
     * @param annons 
     * @return
     */
    public static List<FieldErrorInfo> validate(Object[] args, Annotation[][] annons){
        List<FieldErrorInfo> fieldErrors = new ArrayList<FieldErrorInfo>();
        
        if(annons != null) {
        	
        	for(int i=0; i<annons.length; ++i) {
        		Annotation[] arr = annons[i];
        		if(arr.length > 0) {
        			Object param = args[i];
        			List<FieldErrorInfo> list = ValidationUtils.validate(param);
        			fieldErrors.addAll(list);
        		}
        	}
        }
        
	    return fieldErrors;
    }
    
    
}
