package org.wejar.redis.mq;

/**
 * Redis MQ 消息工人
 * @ClassName:  RedisMQWorker   
 * @author: WejarChan
 * @date:   2018年10月28日 下午11:10:09
 */
public interface RedisMQWorker {

	/**
	 * 消费工作，执行成功返回true
	 * 如用于
	 * @Title: work   
	 * @param message
	 * @return boolean      
	 * @throws
	 */
	public boolean work(String message);
	
	/**
	 * 工人名字，任务名字，用于给线程设置名字
	 * @Title: getName   
	 * @return String      
	 * @throws
	 */
	public String getName();
}
