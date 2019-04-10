package org.wejar.genid.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wejar.genid.annotation.GenIdType;
import org.wejar.genid.annotation.WejarGenId;

import com.alibaba.fastjson.JSON;

/**
 * 注解id生成器，mybatis拦截器实现
 * @ClassName:  SnowflakeIdGeneratorInterceptor   
 * @author: WejarChan
 * @date:   2018年10月26日 上午5:55:13
 */
@Intercepts(value = {
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }), })
public class IdGeneratorInterceptor extends AbstractAnnoIdGenerator implements Interceptor {

	private Logger logger = LoggerFactory.getLogger(IdGeneratorInterceptor.class);

	@Override
    public Object intercept(Invocation invocation) throws Throwable {

    	Object target = invocation.getTarget();
        Object result = null;
        if (target instanceof Executor) {

        	final Object[] args = invocation.getArgs();
        	//获取原始的ms
        	MappedStatement ms = (MappedStatement) args[0];
        	
			if (ms.getSqlCommandType().equals(SqlCommandType.INSERT)) {
				logger.debug("拦截到INSERT方法,进入AnnoId生成流程。");
				// 是插入方法
				// 获取他的参数
				Object parameterObject = args[1];
				Class<? extends Object> paramClazz = parameterObject.getClass();
				Field[] fields = paramClazz.getDeclaredFields();

				boolean accessible;
				for (Field field : fields) {
					WejarGenId autoGenId = field.getAnnotation(WejarGenId.class);
					if (autoGenId != null) {
						logger.debug("找到AutoGenId注解，属性名:" + field.getName());
						// 获取到要生成的id类型
						GenIdType type = autoGenId.type();
						Object genId = getNextId(type);
						if (genId != null) {
							// 开始设置Id属性值
							accessible = field.isAccessible();
							field.setAccessible(true);
							// 取得该字段的值
							Object value = field.get(parameterObject);
							if (value == null) {
								logger.debug("目标属性为null，生成SnowflakeId。");
								// 空值为其生成设置genId值
								field.set(parameterObject, genId.toString());
								logger.debug("genId:" + genId + "\t设置成功。");
							} else {
								logger.debug("目标属性不为null，跳过。");
							}
							field.setAccessible(accessible);
						}
					}
				}
			}
        }
        /**执行方法*/
        result = invocation.proceed();
        return result;
    }

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);

	}

	@Override
	public void setProperties(Properties properties) {
	}
}
