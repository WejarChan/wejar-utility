package org.wejar.redis.mq;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * Redis MQ 实现类
 * @ClassName:  RedisMQProducer   
 * @author: WejarChan
 * @date:   2018年10月28日 下午11:14:37
 */
public class RedisMQ {

	private final Logger logger = LoggerFactory.getLogger(RedisMQ.class);
	
    private JedisPool jedisPool;

	private Timer delayQueueObserver;

	private void init() {
		this.delayQueueObserver = new Timer("redis MQ 延时队列观察者线程");
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {  
            @Override 
            public void run()  
            {  
            	logger.info("进入redisMQ销毁方法。");
            	if(delayQueueObserver != null) {
                	delayQueueObserver.cancel();
                	logger.info("停止[延时队列观察者]定时器。");
                }
            }  
        }));  
	}
	
	public RedisMQ(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		init();
	}
	
	public boolean produce(String channel,String message) {
		if(channel == null) {
			throw new IllegalArgumentException("channel 不能为 null");
		}
		if(message == null) {
			throw new IllegalArgumentException("message 不能为 null");
		}
		String apiName = "redisMQ-produce";
		String waitQueue = RedisMQKeys.WAIT_PREFIX + channel;
		logger.debug("进入--{},参数-channel:{},message:{}",apiName,channel,message);
		
		Jedis jedis = this.jedisPool.getResource();
		long count = 0;
		try {
    		count = jedis.lpush(waitQueue, message);
    		logger.debug("{}--保存消息成功，存放队列-队列名:{}",apiName,waitQueue);
    	}catch(Exception e) {
    		logger.error("{}--保存消息异常。原因:{}",apiName,e.getMessage());
    	}finally {
    		jedis.close();
    	}
		return count > 0;
	}
	
	/**
	 * 消费一条消息,若当前无该channel的消息，则返回null
	 * @Title: comsume   
	 * @param channel
	 * @return String      
	 * @throws
	 */
	public String comsume(String channel) {
		if(channel == null) {
			throw new IllegalArgumentException("channel 不能为 null");
		}
		String apiName = "RedisMQ-comsume";
		logger.debug("进入--{}，参数-channel:{}",apiName,channel);
		String waitQueue = RedisMQKeys.WAIT_PREFIX + channel;
		Jedis jedis = this.jedisPool.getResource();
		String message = null;
		try {
			logger.debug("{}--开始从等待队列获取消息，队列名:{}",apiName,waitQueue);
			message = jedis.rpop(waitQueue);
			logger.debug("{}--获取消息成功。channel:{},message:{}",apiName,channel,message);
		}catch(Exception e) {
			logger.error("{}--获取消息发生异常。原因:{}",apiName,e.getMessage());
		}finally {
			jedis.close();
		}
		return message;
	}
	
    /**
     * @return 
     * 
     * @Title: publish   
     * @param channel  消息发布订阅 主题
     * @param message  消息信息    
     * @throws
     */
    public boolean publish(String channel, String message) {
    	if(channel == null) {
			throw new IllegalArgumentException("channel 不能为 null");
		}
    	if(message == null) {
			throw new IllegalArgumentException("message 不能为 null");
		}
    	
    	String apiName = "RedisMQ-publish";
    	
    	logger.debug("进入--{}，参数-channel:{},message:{}",apiName,channel,message);
    	
    	Jedis jedis = this.jedisPool.getResource();
    	long count = 0;
    	try {
    		count = jedis.publish(channel, message);
    		logger.debug("{}--发布消息成功! channel:{}",apiName,channel);
    	}catch(Exception e) {
    		logger.error("{}--发布消息异常。原因:{}",apiName,e.getMessage());
    	}finally {
    		jedis.close();
    	}
    	return count > 0;
    }
    
    /**
     * 取消一个延时消息
     * @Title: cancelDelayMessage   
     * @param messageId
     * @return boolean      
     * @throws
     */
    public boolean cancelDelayMessage(String messageId) {
    	if(messageId == null) {
    		throw new IllegalArgumentException("messageId 不能为 null");
    	}
    	String apiName = "RedisMQ-cancelDelayMessage";
    	
    	logger.debug("进入--{},参数-messageId:{}",apiName,messageId);
    	
    	Jedis jedis = this.jedisPool.getResource();
    	Long count = 0L;
    	try {
    		count = jedis.del(RedisMQKeys.DELAY_POOL + messageId);
    		logger.debug("{}--取消延时消息成功! messageId:{}",apiName,messageId);
    	}catch(Exception e) {
    		logger.error("{}--取消延时消息异常。原因:{}",apiName,e.getMessage());
    	}finally {
    		jedis.close();
    	}
    	return count > 0;
    }
    
    public boolean produceDelayMessage(String channel,String message,Date expectTime) {
    	return produceDelayMessage( channel, message, expectTime,null);
    }
    
	public boolean produceDelayMessage(String channel,String message,Date expectTime,String messageId) {
		if(channel == null) {
			throw new IllegalArgumentException("channel 不能为 null");
		}
		if(message == null) {
			throw new IllegalArgumentException("message 不能为 null");
		}
		if(expectTime == null) {
			throw new IllegalArgumentException("expectTime 不能为 null");
		}
		String apiName = "RedisMQ-produceDelayMessage";
		logger.debug("进入--{},参数-channel:{},message:{},expectTime:{},messageId:{}",apiName,channel,message,expectTime,messageId);
		Jedis jedis = this.jedisPool.getResource();
		try {
			if(expectTime.getTime()/1000 <= System.currentTimeMillis()/1000) {
				//小于等于当前时间,则存放到waiting队列
				produce(channel,message);
				logger.debug("{}--期望时间小于当前时间,存放进等待队列成功。返回true",apiName);
				return true;
			}
			//创建delayMessage
			long currMillis = System.currentTimeMillis();
			DelayMessage delayMsg = new DelayMessage(channel,message,currMillis);
			String jsonDelayMsg = JSON.toJSONString(delayMsg);
			double score = expectTime.getTime()/1000;
			if(messageId == null || messageId.trim().length() <= 0) {
				messageId = UUID.randomUUID().toString();
				logger.debug("{}--messageId 为空，自动生成messageId:{}",apiName,messageId);
			}
			Transaction trans = jedis.multi();
			//存放进消息池中
			String key = RedisMQKeys.DELAY_POOL + messageId;
			trans.set(key, jsonDelayMsg);
			logger.debug("{}--将消息存放进消息池成功。key:{}",apiName,key);
			//存放进延时队列中
			trans.zadd(RedisMQKeys.DELAY_QUEUE, score, messageId);
			logger.debug("{}--将消息存放进延时队列成功，队列名:{}",apiName,RedisMQKeys.DELAY_QUEUE);
			List<Object> results = trans.exec();
			logger.debug("{}--执行事物完成,事物结果:{}",apiName,results);
			return results != null ;
		}catch(Exception e) {
			logger.error("{}--生产延时消息发生异常。原因:{}",apiName,e.getMessage());
		}finally {
			jedis.close();
		}
		return false;
	}
	
	
	/**
	 * 延时消息队列，时间不敏感。最小单位为分钟
	 * @Title: produce   
	 * @param channel
	 * @param message
	 * @param delay
	 * @param timeUnit 最小是分钟
	 * @return boolean      
	 * @throws
	 */
	public boolean produceDelayMessage(String channel,String message,long delay,TimeUnit timeUnit,String messageId) {
		if(channel == null) {
			throw new IllegalArgumentException("channel 不能为 null");
		}
		if(message == null) {
			throw new IllegalArgumentException("message 不能为 null");
		}
		if(delay <= 0) {
			throw new IllegalArgumentException("delay 必须大于 0");
		}
		if(timeUnit == null) {
			throw new IllegalArgumentException("timeUnit 不能为 null");
		}
		if(
//				timeUnit.equals(TimeUnit.SECONDS) || 
				timeUnit.equals(TimeUnit.MILLISECONDS) || 
				timeUnit.equals(TimeUnit.MICROSECONDS) || 
				timeUnit.equals(TimeUnit.NANOSECONDS)) {
			throw new IllegalArgumentException("timeUnit 最小为 TimeUnit.SECONDS");
		}
		Date expectTime = new Date(System.currentTimeMillis()+ timeUnit.toMillis(delay));
		
		return produceDelayMessage(channel,message,expectTime,messageId);
	}
	
	public boolean produceDelayMessage(String channel,String message,long delay,TimeUnit timeUnit) {
		return produceDelayMessage( channel, message, delay, timeUnit, null) ;
	}
	
	/**
	 * 开始观察延时队列
	 * @Title: startObserver   
	 * @throws
	 */
	public void startObserver(int secound) {
		if(secound <= 0) {
			throw new IllegalArgumentException("secound 必须大于 1");
		}
		final RedisMQ instance = this;
		this.delayQueueObserver.schedule(new TimerTask() {
            @Override
            public void run() {
            	instance.flushExpriedDelayingMsg();
            }
        }, 3000 , TimeUnit.SECONDS.toMillis(secound));
		logger.info("观察延时队列任务已启动，观察周期:{}秒",secound);
	}
	
	/**
	 * 停止观察延时队列
	 * @Title: stopObserver   
	 * @throws
	 */
	public void stopObserver() {
		this.delayQueueObserver.cancel();
		logger.info("观察延时队列任务已停止。");
	}
	
	/**
	 * 将延时队列中超时信息转移至等待队列
	 * @Title: flushExpriedDelayingMsg   
	 * @return	转移条目数量      
	 * @throws
	 */
	public int flushExpriedDelayingMsg() {
		String apiName = "RedisMQ-flushExpiredDelayingMsg";
		logger.debug("进入--{}",apiName);
		Jedis jedis = this.jedisPool.getResource();
		try {
			long currSec = System.currentTimeMillis()/1000;
			Set<String> timeoutSet = jedis.zrangeByScore(RedisMQKeys.DELAY_QUEUE, 0d, currSec);
			if(timeoutSet == null || timeoutSet.isEmpty()) {
				logger.debug("{}--无超时消息，退出。当前时间:{}秒",apiName,currSec);
				return 0;
			}
			logger.debug("{}--获得 {} 条超时消息,开始处理消息。",apiName,timeoutSet.size());
			
			int successCount = 0;
			int removeCount = 0;
			Iterator<String> it = timeoutSet.iterator();
			while(it.hasNext()) {
				String messageId = it.next();
				
				String jsonMsg = jedis.get(RedisMQKeys.DELAY_POOL+messageId);
				if(jsonMsg == null || jsonMsg.trim().length() <= 0) {
					logger.debug("{}--获得消息内容为空,抛弃该消息。messageId:{}",apiName,messageId);
					Transaction trans = jedis.multi();
					trans.del(RedisMQKeys.DELAY_POOL + messageId);
					trans.zrem(RedisMQKeys.DELAY_QUEUE, messageId);
					List<Object> results = trans.exec();
					if(results != null) {
						++removeCount;
					}
					continue;
				}
				DelayMessage delayMsg = null;
				try {
					delayMsg = JSON.parseObject(jsonMsg, DelayMessage.class);
				}catch(Exception e) {
					logger.debug("{}--解析[延时消息]异常,抛弃该消息。messageId:{}",apiName,messageId);
					//删除消息
					Transaction trans = jedis.multi();
					trans.del(RedisMQKeys.DELAY_POOL + messageId);
					trans.zrem(RedisMQKeys.DELAY_QUEUE, messageId);
					List<Object> results = trans.exec();
					if(results != null) {
						++removeCount;
					}
					continue;
				}
				logger.debug("{}--获取[延时消息]成功。channel:{},createTime:{},messageId:{}",apiName,delayMsg.getChannel(),delayMsg.getCreateTime(),messageId);
				Transaction trans = jedis.multi();
				String waitQueue = RedisMQKeys.WAIT_PREFIX + delayMsg.getChannel();
				trans.del(RedisMQKeys.DELAY_POOL + messageId);
				trans.lpush(waitQueue, delayMsg.getBody());
				trans.zrem(RedisMQKeys.DELAY_QUEUE, messageId);
				List<Object> results = trans.exec();
				if(results != null) {
					++successCount;
					logger.debug("{}--消息转存到待消费队列成功! 队列名:{}",apiName,waitQueue);
				}else {
					logger.error("{}--消息转存到待消费队列失败，事物执行失败! 事物结果:{}",apiName,results);
				}
			}
			logger.debug("{}--转存消息到待消费队列完成。成功转换 {} 条消息,抛弃 {} 条消息。",apiName,successCount,removeCount);
			return successCount;
			
		}catch(Exception e) {
			logger.error("{}--观察处理延时消息异常，原因:{}",apiName,e.getMessage());
		}finally {
			jedis.close();
		}
		
		return 0;
	}
	
	/**
	 * 使用订阅者订阅消息
	 * @param jedisPubSub - 监听任务
	 * @param channels - 要监听的消息通道
	 */
	public void subscribe(RedisMQSubscriber subscriber, String... channels) {
		if(subscriber == null) {
			throw new IllegalArgumentException("subscriber 不能为null");
		}
		if(channels == null || channels.length <= 0	) {
			throw new IllegalArgumentException("channels 不能为null,最小长度为1");
		}
		String apiName = "RedisMQ-subscribe";
		
		logger.debug("进入--{}，参数-subscriber:{},channels:{}",apiName,subscriber,Arrays.toString(channels));
		Jedis jedis = this.jedisPool.getResource();
		try {
			jedis.subscribe(subscriber, channels);
			logger.debug("{}--消息订阅成功。");
		}catch(Exception e) {
			logger.error("{}--消息订阅发生异常，原因:{}",apiName,e.getMessage());
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 生产消费模式-消费者
	 * 创建一个监听 channel的 redis MQ 消费者
	 * @Title: createMQComsumer   
	 * @param worker
	 * @param channel
	 * @return RedisMQComsumer      
	 * @throws
	 */
	public RedisMQComsumer createMQComsumer(RedisMQWorker worker,String channel) {
		return new RedisMQComsumer(this.jedisPool, channel, worker);
	}
	
	/**
	 * 发布订阅模式-订阅者
	 * 创建一个订 redis MQ 订阅者
	 * @Title: createMQSubscriber   
	 * @param worker
	 * @return RedisMQSubscriber      
	 * @throws
	 */
	public RedisMQSubscriber createMQSubscriber(RedisMQWorker worker) {
		return new RedisMQSubscriber(worker);
	}
	/**
	 * 发布订阅模式-订阅者
	 * 创建一个订阅 channel的 redis MQ 订阅者
	 * @Title: createMQSubscriber   
	 * @param worker
	 * @param channel
	 * @return RedisMQSubscriber      
	 * @throws
	 */
	public RedisMQSubscriber createMQSubscriber(RedisMQWorker worker,String channel) {
		RedisMQSubscriber subscriber = new RedisMQSubscriber(worker);
		subscribe(subscriber,channel);
		return subscriber;
	}
	
}
