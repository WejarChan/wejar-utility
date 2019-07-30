package org.wejar.redis.mq;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *  Redis MQ 生产-消费 消费者
 * @ClassName:  RedisMQComsumer   
 * @author: WejarChan
 * @date:   2018年10月28日 下午11:13:34
 */
public class RedisMQComsumer extends Thread {

	private final Logger logger = LoggerFactory.getLogger(RedisMQSubscriber.class);
	
	private String channel;
	private String waitQueue;
	private String workQueue;
	
    private RedisTemplate<String, String> redisTemplate;
	
	private RedisMQWorker worker;
	
	/**
	 * 线程执行标志位
	 */
	private boolean running = false;

	@Override
	public synchronized void start() {
		this.running = true;
		logger.info("启动--MQComsumer，线程号:{}",this.getId());
		super.start();
	}
	
	/**
	 * 取消任务，停止线程
	 * @Title: cancel   
	 * @throws
	 */
	public synchronized void cancel() {
		logger.info("停止--MQComsumer，线程号:{}",this.getId());
		this.running = false;
	}


	protected RedisMQComsumer(RedisTemplate<String,String> redisTemplate,String channel,RedisMQWorker worker) {
		super(worker.getName());
		this.redisTemplate = redisTemplate;
		this.channel = channel;
		this.waitQueue = RedisMQKeys.WAIT_PREFIX + channel;
		this.workQueue = RedisMQKeys.WORK_PREFIX + channel;
		this.worker = worker;
	}
	
	@Override
	public void run() {
		String apiName = "RedisMQComsumer-comsume";
		while(running) {
			try {
				logger.debug("{}--开始从等待队列获取消息，队列名：{},channel:{}",apiName,waitQueue,this.channel);
				String message = null;
				try {
					message = this.redisTemplate.opsForList().rightPopAndLeftPush(waitQueue, workQueue);
					logger.debug("{}--获取消息成功！message:{}",apiName,message);
				}catch(Exception e) {
					logger.error("{}--获取消息超时!继续执行。",apiName);
					continue;
				}
				logger.debug("{}--开始调用worker执行任务。workerClass:{}",apiName,worker.getClass().getName());
				boolean finished = false;
				try {
					finished = this.worker.work(message);
				}catch(Exception e) {
					logger.error("{}--调用worker执行任务发生异常，原因：{}",apiName,e.getMessage() != null ? e.getMessage() : e.getClass());
				}
				if(finished) {
					this.redisTemplate.opsForList().remove(workQueue, -1, message);
					logger.debug("{}--worker执行任务成功，从工作队列移除消息。队列名:{}",apiName,workQueue);
				}else {
					logger.info("{}--worker执行任务失败，开始转移消息至等待队列。",apiName);
					try {
						sendMessageBack2waitQueue(message);
					}catch(Exception e) {
						logger.error("{}--将失败任务重新存回等待队列异常，原因:{}",apiName,e.getMessage() != null ? e.getMessage() : e.getClass());
					}
				}
			}catch(Exception e) {
				logger.error("{}--消费消息发生异常。原因:{},channel:{}",apiName,e.getMessage() != null ? e.getMessage() : e.getClass(),channel);
			}
		}
	}

	private void sendMessageBack2waitQueue(final String message) {
		String apiName = "sendMessageBack2waitQueue";
		logger.debug("进入--{},参数-message:{},channel:{}",apiName,message,channel);
		try {
			this.redisTemplate.multi();
			this.redisTemplate.opsForList().remove(workQueue, -1, message);
			this.redisTemplate.opsForList().leftPush(waitQueue, message);
			List<Object> resultList = this.redisTemplate.exec();
			String result = (String) resultList.get(0);
			if(result.equalsIgnoreCase("TRUE")) {
				logger.debug("{}--事物执行成功,消息存放至等待队列-队列名：{}",apiName,waitQueue);
			}else {
				logger.error("{}--事物执行失败，事物结果：{}",resultList);
			}
		}catch(Exception e) {
			logger.error("{}--发生异常。原因:{}",apiName,e.getMessage() != null ? e.getMessage() : e.getClass());
		}
	}

}

