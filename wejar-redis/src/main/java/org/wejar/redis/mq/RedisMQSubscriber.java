package org.wejar.redis.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * Redis MQ 发布-订阅 发布者
 * @ClassName:  RedisMQSubscriber   
 * @author: WejarChan
 * @date:   2018年10月28日 下午11:09:31
 */
public class RedisMQSubscriber implements MessageListener {
	
	private final Logger logger = LoggerFactory.getLogger(RedisMQSubscriber.class);
	
    private RedisMQWorker worker;
    
    @Autowired
	private RedisTemplate<String, String> redisTemplate;
    
	/**
     * 订阅接收发布者的消息
     */
    protected RedisMQSubscriber(RedisMQWorker worker) {
		this.worker = worker;
	}
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		byte[] body = message.getBody();
		String msgBody = (String) redisTemplate.getValueSerializer().deserialize(body);
		System.out.println(msgBody);
		byte[] channel = message.getChannel();
		String msgChannel = (String) redisTemplate.getValueSerializer().deserialize(channel);
		System.out.println(msgChannel);
		String msgPattern = new String(pattern);
		System.out.println(msgPattern);
		// TODO Auto-generated method stub
		logger.debug("redis MQ 订阅者，接收到消息! channel:{},body:{}",channel,message);
    	this.worker.work(msgBody);
	}
}