package org.wejar.redis.mq;

import java.io.Serializable;

class DelayMessage implements Serializable{
	
	private static final long serialVersionUID = -3458805208491112075L;
	
	private String channel;
	private String body;
	private Long createTime;
	
	
	public DelayMessage(String channel, String body,long createTime) {
		super();
		this.channel = channel;
		this.body = body;
		this.createTime = createTime;
	}
	
	public DelayMessage() {
		super();
	}

	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

}
