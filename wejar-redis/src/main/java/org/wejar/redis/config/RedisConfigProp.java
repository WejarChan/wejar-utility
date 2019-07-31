package org.wejar.redis.config;

//@Component  
//@ConfigurationProperties(prefix="wejar.redis") //接收application.yml中的myProps下面的属性  
public class RedisConfigProp {
    private Integer delayingQueuePeriod;

    
    public RedisConfigProp() {
    }
    
	public Integer getDelayingQueuePeriod() {
		return delayingQueuePeriod;
	}

	public void setDelayingQueuePeriod(Integer delayingQueuePeriod) {
		this.delayingQueuePeriod = delayingQueuePeriod;
	}
}
