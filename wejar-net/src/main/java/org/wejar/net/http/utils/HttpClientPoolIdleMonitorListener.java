package org.wejar.net.http.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author "Jiance Qin"
 * 
 * @date 2016年6月22日
 * 
 * @time 上午11:10:30
 * 
 * @desc 空闲连接踢出策略的启动和销毁，适用于web工程 ServletListener生命周期
 * 
 */
public class HttpClientPoolIdleMonitorListener implements ServletContextListener {

	private static ScheduledExecutorService execs = null;
	private static LinkedBlockingQueue<HttpClientPoolIdleMonitorTask> taskQueue = new LinkedBlockingQueue<HttpClientPoolIdleMonitorTask>();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		execs = Executors.newScheduledThreadPool(5);
		while (!taskQueue.isEmpty()) {
			scheduledTask(taskQueue.poll());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		execs.shutdown();
	}

	public static void scheduledTask(HttpClientPoolIdleMonitorTask task) {
		if (execs != null && !execs.isShutdown() && !execs.isTerminated()) {
			execs.scheduleAtFixedRate(task, 5, 5000, TimeUnit.MILLISECONDS);
		} else {
			taskQueue.offer(task);
		}
	}
}
