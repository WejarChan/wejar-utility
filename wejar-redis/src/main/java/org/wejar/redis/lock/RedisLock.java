package org.wejar.redis.lock;

import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
* @ClassName: RedisLock
* @Description: 基于Redis的分布式锁工具
* @author wejarchan
* @date 2017年8月24日 下午2:55:16 
*
 */
public class RedisLock {

    private static final String DEFAULT_KEY_PREFIX = "WEJAR:LOCK:";

	private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private JedisPool jedisPool;
    
    private LockKeyNamingRule lockKeyNamingRule;

    public RedisLock(JedisPool jedisPool) {
    	this.jedisPool = jedisPool;
    }
    
    /**
     * 获取锁,参数可选
     * @param key			
     * @param expiresMillis		redis的有效期,锁自动过时
     * @return true 成功获取锁，false 不成功
     */
    public boolean lock(String key,Long expiresMillis){
    	//分布式锁的key规则
    	Jedis jedis = this.jedisPool.getResource();
    	key = processKey(key);
    	logger.debug("RedisLock locking key:{},expiresMillis:{}",key,expiresMillis);
    	boolean result = this.setNX(jedis,key, "LOCK" ,expiresMillis);
    	jedis.close();
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
    	key = processKey(key);
    	Jedis jedis = this.jedisPool.getResource();
    	jedis.del(key);
    	jedis.close();
    }
    
    /**
     * 锁key规则
    * @Title: processKeyRule 
    * @param key
    * @return
    * @throws
     */
    private String processKey(String key){
    	if(this.lockKeyNamingRule == null){
    		this.lockKeyNamingRule = new PrefixLockKeyNamingRule(DEFAULT_KEY_PREFIX);
    	}
    	return this.lockKeyNamingRule.processName(key);
    }
    

    @SuppressWarnings("unused")
	private boolean setNX(Jedis jedis,final String key, final String value) {
    	String json = JSON.toJSONString(value);
    	Long count = jedis.setnx(key, json);
    	return count == 1;
    }
    
	@SuppressWarnings("unused")
	private String getSet(Jedis jedis,final String key, final String value) {
    	String oldValue = jedis.getSet(key, value);
        return oldValue;
	}

	private boolean setNX(Jedis jedis, final String key, String value,final Long expiresMillis) {
		Long count = jedis.setnx(key, value);
		if(count == 1 && expiresMillis != null) {
			jedis.pexpire(key, expiresMillis);
		}
		return count == 1;
    }

	@SuppressWarnings("unused")
	private String getSet(Jedis jedis,final String key, final String value,final Long expiresMillis) {
    	String oldValue = jedis.getSet(key, value);
    	if(expiresMillis != null){
    		jedis.pexpire(key, expiresMillis);
    	}
        return oldValue;
    }
    
	public void setLockKeyNamingRule(LockKeyNamingRule lockKeyNamingRule) {
		this.lockKeyNamingRule = lockKeyNamingRule;
	}
}

