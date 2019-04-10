package org.wejar.weixin.mp;

import org.wejar.dom.annotation.XStreamCDATA;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class TextMessage {
	@XStreamCDATA  
	@XStreamAlias("MsgType")
	private String msgType;
	@XStreamCDATA  
	@XStreamAlias("FromUserName")
	private String fromUserName;
	@XStreamCDATA 
	@XStreamAlias("ToUserName")
	private String toUserName;
	@XStreamAlias("CreateTime")
	private Long createTime;
	@XStreamCDATA 
	@XStreamAlias("Content")
	private String content;

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	
}
