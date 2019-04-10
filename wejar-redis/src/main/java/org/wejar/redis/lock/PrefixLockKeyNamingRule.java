package org.wejar.redis.lock;


public class PrefixLockKeyNamingRule implements LockKeyNamingRule {

	private String prefix;
	
	public PrefixLockKeyNamingRule(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public String processName(String key) {
		return this.prefix+key;
	}

}
