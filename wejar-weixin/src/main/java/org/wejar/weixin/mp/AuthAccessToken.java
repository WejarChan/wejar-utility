package org.wejar.weixin.mp;

public class AuthAccessToken {
//	access_token":"ACCESS_TOKEN",    
//	 "expires_in":7200,    
//	 "refresh_token":"REFRESH_TOKEN",    
//	 "openid":"OPENID",    
//	 "scope":"SCOPE" } 
	
	private String accessToken;
	private long deadLine;
	private String refreshToken;
	
	private String openid;
	
	private String scope;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public long getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(long deadLine) {
		this.deadLine = deadLine;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	
}
