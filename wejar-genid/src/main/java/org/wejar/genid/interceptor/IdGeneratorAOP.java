package org.wejar.genid.interceptor;


import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.wejar.genid.annotation.WejarGenId;
import org.wejar.genid.annotation.GenIdType;
import org.wejar.genid.generator.SnowflakeIdGenerator;

//@Aspect
//@Component
//@Order(0)
public class IdGeneratorAOP extends AbstractAnnoIdGenerator {

	private Logger logger = LoggerFactory.getLogger(IdGeneratorAOP.class);
	
    @Autowired
    SnowflakeIdGenerator snowflakeIdWorker;
    
	@Pointcut("execution(* org.wejar.shuagou.mapper..insert*(..))")  
    public void mapperInsertMethodPointcut(){}  
	
//	@Before("mapperInsertMethodPointcut()")
//	public void before(JoinPoint jp){
//		
//	}
	
	@Around("mapperInsertMethodPointcut()") //指定拦截器规则；也可以直接把“execution(* com.xjj.........)”写进这里  
	public Object validation(ProceedingJoinPoint pjp) throws Throwable{
		logger.debug("进入AnnoId生成器AOP");
		//获取目标方法的参数信息  
		Object[] args = pjp.getArgs();  
		try{
			for(Object parameterObject : args){
				Class<? extends Object> paramClazz = parameterObject.getClass();
				
	            Field[] fields = paramClazz.getDeclaredFields();
	            
	            boolean hasAutoGenIdAnno = false;
                for(Field field : fields){
                	WejarGenId autoGenId = field.getAnnotation(WejarGenId.class);
//                    boolean autoGenId = field.isAnnotationPresent(AutoGenId.class);
                	hasAutoGenIdAnno = true;
                    if(autoGenId != null){
                    	logger.debug("找到AutoGenId注解，属性名:"+field.getName());
                    	//获取到要生成的id类型
                    	GenIdType type = autoGenId.type();
                    	Object genId = getNextId(type);
                    	if(genId != null) {
                    		//开始设置Id属性值
                    		boolean accessible = field.isAccessible();
                    		field.setAccessible(true);
                    		//取得该字段的值
                    		Object value = field.get(parameterObject);
                    		if(value == null){
                    			logger.debug("目标属性为null，生成SnowflakeId。");
                    			//空值为其生成设置genId值
                    			field.set(parameterObject,genId.toString());
                    			logger.debug("genId:"+genId+"\t设置成功。");
                    		}else{
                    			logger.debug("目标属性不为null，跳过。");
                    		}
                    		field.setAccessible(accessible);
                    	}
                    }
                }
                if(!hasAutoGenIdAnno){
                    logger.debug("没有找到SnowflakeId注解，结束SnowflakeId生成流程。");
                }
	            
			}
		} catch (IllegalAccessException e) {
			logger.error("AnnoId生成失败：反射获取属性异常,属性不可访问异常",e);
			throw e;
		}
		
		logger.debug("结束SnowflakeId生成");
		Object result = pjp.proceed();
		return result;
	 
	}
	
}
