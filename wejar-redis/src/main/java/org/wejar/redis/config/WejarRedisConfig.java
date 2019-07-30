package org.wejar.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.wejar.redis.lock.RedisLock;
import org.wejar.redis.mq.RedisMQ;

@Configuration
public class WejarRedisConfig implements BeanPostProcessor{

    private static final Logger logger = LoggerFactory.getLogger(WejarRedisConfig.class);
    
//	@Autowired
//    private RedisConfigProp redisCofigProp;
    
    @Value("${wejar.redis.delayingQueuePeriod}")
    private Integer delayingQueuePeriod;

    
    public WejarRedisConfig(){
        logger.info("init RedisConfig...");
    }
    
    @Bean
    @ConfigurationProperties(prefix="wejar.redis") 
    public RedisConfigProp redisConfigProp() {
    	RedisConfigProp prop = new RedisConfigProp();
    	prop.setDelayingQueuePeriod(delayingQueuePeriod);
    	return prop;
    }
    
    @Bean
    public RedisLock redisLock(RedisTemplate<String, String> redisTemplate) {
    	RedisLock lock = new RedisLock(redisTemplate);
    	return lock;
    }
    
    
//    @Bean
//    @ConditionalOnProperty(prefix="wejar.redis",name= {"enableMQ"})
//    public RedisMQ redisMQ(RedisConfigProp redisConfigProp,RedisTemplate<String, String> redisTemplate) {
//    	logger.info("init RedisMQ...");
//    	RedisMQ redisMQ = new RedisMQ(redisTemplate);
//    	if(redisConfigProp.getDelayingQueuePeriod() != null) {
//    		if(redisConfigProp.getDelayingQueuePeriod() < 1) {
//    			throw new IllegalArgumentException("Redis MQ 初始化参数 delayingQueuePeriod 必须大于 1");
//    		}
//    		redisMQ.startObserver(redisConfigProp.getDelayingQueuePeriod());
//    	}
//    	return redisMQ;
//    }
    

    
}
