package org.wejar.redis.lock;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * 
* @ClassName: RedisLock
* @Description: 基于Redis的分布式锁工具
* @author wejarchan
* @date 2017年8月24日 下午2:55:16 
*
 */
public class RedisLock {

	private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

	private RedisTemplate<String, String> redisTemplate;

    public RedisLock(RedisTemplate<String, String> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
	}

    /**
     * 获取锁,参数可选
     * @param key			
     * @param expiresMillis		redis的有效期,锁自动过时
     * @return true 成功获取锁，false 不成功
     */
    public boolean lock(String key,Long expiresMillis){
    	//分布式锁的key规则
    	logger.debug("RedisLock locking key:{},expiresMillis:{}",key,expiresMillis);
    	ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
    	Boolean result = this.redisTemplate.opsForValue().setIfAbsent("key", "LOCK");
    	this.redisTemplate.expire(key, expiresMillis, TimeUnit.MILLISECONDS);
    	return result;
    }

    /**
     * @throws LockAcquiryWaitTimeoutException 
     * 
    * @Title: lock 
    * @param key
    * @return
    * @throws
     */
    public boolean lock(String key){
    	return lock(key,null);
    }
    
    /**
     * 解锁分布式锁
    * @Title: unlock 
    * @param key
    * @throws
     */
    public void unlock(String key){
    	//分布式锁的key规则
    	this.redisTemplate.delete(key);
    }
    
}

