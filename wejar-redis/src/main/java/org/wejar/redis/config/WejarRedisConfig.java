package org.wejar.redis.config;

import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.wejar.redis.lock.RedisLock;
import org.wejar.redis.mq.RedisMQ;

@Configuration
public class WejarRedisConfig implements BeanPostProcessor{

    private static final Logger logger = LoggerFactory.getLogger(WejarRedisConfig.class);
    
//	@Autowired
//    private RedisConfigProp redisCofigProp;
    
    @Value("${wejar.redis.delayingQueuePeriod:30}")
    private Integer delayingQueuePeriod;

    
    public WejarRedisConfig(){
        logger.debug("init RedisConfig...");
    }
    
    @Bean
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
    
    
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
    
    @Bean
    public RedisMQ redisMQ(RedisConfigProp redisConfigProp,RedisTemplate<String, String> redisTemplate) {
    	logger.info("init RedisMQ...");
    	RedisMQ redisMQ = new RedisMQ(redisTemplate);
    	if(redisConfigProp.getDelayingQueuePeriod() != null) {
    		if(redisConfigProp.getDelayingQueuePeriod() < 1) {
    			throw new IllegalArgumentException("Redis MQ 初始化参数 delayingQueuePeriod 必须大于 1");
    		}
    		redisMQ.startObserver(redisConfigProp.getDelayingQueuePeriod());
    	}
    	return redisMQ;
    }
    

    
}
