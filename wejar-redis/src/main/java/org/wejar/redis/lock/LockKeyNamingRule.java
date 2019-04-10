package org.wejar.redis.lock;

public interface LockKeyNamingRule {

	/**
	 * 加工key
	 * @param key
	 * @return
	 */
	String processName(String key);
}
