package org.wejar.genid.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wejar.genid.annotation.GenIdType;
import org.wejar.genid.generator.SnowflakeIdGenerator;
import org.wejar.genid.interceptor.IdGeneratorInterceptor;

@Configuration
@ConditionalOnProperty(prefix="wejar.genId",name="enable")
public class GenIdConfig {
	
	private Logger logger = LoggerFactory.getLogger(GenIdConfig.class);

    @Value("${wejar.genId.snowFlakeId.workerId}")
    private Integer workerId;

    @Value("${wejar.genId.snowFlakeId.datacenterId}")
    private Integer datacenterId;

    @Bean
    @ConditionalOnProperty(prefix="wejar.genId.snowFlakeId",name= {"workerId","datacenterId"})
    public SnowflakeIdGenerator snowflakeIdWorker(){
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(workerId,datacenterId);
        return snowflakeIdGenerator;
    }

    @Bean
    public IdGeneratorInterceptor annoIdGeneratorInterceptor(
    		@Autowired(required=false)SnowflakeIdGenerator snowflakeIdGenerator
    		) {
    	IdGeneratorInterceptor interceptor = new IdGeneratorInterceptor();
    	logger.debug("创建Id自动生成器拦截器！");
    	if(snowflakeIdGenerator != null) {
    		interceptor.registGenerator(GenIdType.SnowFlakeId, snowflakeIdGenerator);
    		logger.debug("雪花Id生成器注册成功！");
    	}
    	return interceptor;
    }
}
