package org.wejar.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.wejar.redis.lock.RedisLock;
import org.wejar.redis.mq.RedisMQ;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class WejarRedisConfig implements BeanPostProcessor{

    private static final Logger logger = LoggerFactory.getLogger(WejarRedisConfig.class);
    
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.pool.max-wait}")
    private Integer maxWait;
    @Value("${spring.redis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private Integer  minIdle;
    @Value("${spring.redis.timeout}")
    private Integer timeout;
    
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
    @ConfigurationProperties(prefix="spring.redis.jedis.pool")
    public JedisPoolConfig jedisPoolConfig() {
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    	jedisPoolConfig.setMaxTotal(maxActive);
    	jedisPoolConfig.setMaxIdle(maxIdle);
    	jedisPoolConfig.setMaxWaitMillis(maxWait);;
    	jedisPoolConfig.setMinIdle(minIdle);
    	return jedisPoolConfig;
    }
    
    @Bean
    @ConfigurationProperties(prefix="spring.redis.jedis")
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig) {
    	return new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
    }
    
    
    @Bean
    @ConditionalOnProperty(prefix="wejar.redis",name= {"enableRedisLock"})
    public RedisLock redisLock(JedisPool jedisPool) {
        logger.info("init RedisLock...");
        return new RedisLock(jedisPool);
    }
    
    @Bean
    @ConditionalOnProperty(prefix="wejar.redis",name= {"enableMQ"})
    public RedisMQ redisMQ(RedisConfigProp redisConfigProp,JedisPool jedisPool) {
    	logger.info("init RedisMQ...");
    	RedisMQ redisMQ = new RedisMQ(jedisPool);
    	if(redisConfigProp.getDelayingQueuePeriod() != null) {
    		if(redisConfigProp.getDelayingQueuePeriod() < 1) {
    			throw new IllegalArgumentException("Redis MQ 初始化参数 delayingQueuePeriod 必须大于 1");
    		}
    		redisMQ.startObserver(redisConfigProp.getDelayingQueuePeriod());
    	}
    	return redisMQ;
    }
    

    
}
