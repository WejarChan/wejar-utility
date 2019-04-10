package org.wejar.net.http.utils;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Jiance Qin"
 * 
 * @date 2016年6月22日
 * 
 * @time 上午10:41:56
 * 
 * @desc HttpComponents空闲连接踢出线程 默认策略：5秒执行一次，踢出过期的idle连接，同时关闭空闲60S以上的连接
 * 
 */
public class HttpClientPoolIdleMonitorTask implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(HttpClientPoolIdleMonitorTask.class);
	private final HttpClientConnectionManager connMgr;

	public HttpClientPoolIdleMonitorTask(HttpClientConnectionManager connMgr) {
		super();
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		synchronized (connMgr) {
			logger.debug("Close expired and idle connections...");
			// 关闭失效的连接
			connMgr.closeExpiredConnections();
			// 可选的, 关闭60秒内不活动的连接
			connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
			logger.debug("Close expired and idle connections finished.");
		}
	}

}
