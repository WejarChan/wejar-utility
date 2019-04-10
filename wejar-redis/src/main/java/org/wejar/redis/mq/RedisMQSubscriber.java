package org.wejar.redis.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;


/**
 * Redis MQ 发布-订阅 发布者
 * @ClassName:  RedisMQSubscriber   
 * @author: WejarChan
 * @date:   2018年10月28日 下午11:09:31
 */
public class RedisMQSubscriber extends JedisPubSub  {
	
	private final Logger logger = LoggerFactory.getLogger(RedisMQSubscriber.class);
	
    private RedisMQWorker worker;
    
	/**
     * 订阅接收发布者的消息
     */
    protected RedisMQSubscriber(RedisMQWorker worker) {
		this.worker = worker;
	}
	
    @Override
    public void onMessage(String channel, String message) {       //收到消息会调用
    	logger.debug("redis MQ 订阅者，接收到消息! channel:{},body:{}",channel,message);
    	this.worker.work(message);
    }
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {    //订阅了频道会调用
    	logger.debug("redis MQ 订阅者，订阅了频道! channel:{},订阅频道数量{}",channel,subscribedChannels);
    }
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {   //取消订阅 会调用
    	logger.debug("redis MQ 订阅者，取消订阅了频道! channel:{},订阅频道数量{}",channel,subscribedChannels);
    }
}