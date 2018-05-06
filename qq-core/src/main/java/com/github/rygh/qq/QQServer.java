package com.github.rygh.qq;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.util.NamedThreadFactory;
import com.github.rygh.qq.util.NumberedNameThreadFactory;

public class QQServer {

	private static final Logger logger = LoggerFactory.getLogger(QQServer.class);
	
	private final ScheduledExecutorService poller;
	private final ThreadPoolExecutor pool;
	
	private final QueueConfig config;
	
	public QQServer(QueueConfig config) {
		this.config = config;
		this.poller = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("qq-poller"));
		
		// TODO: Multiple worker pools can be added for groups of consumers
		this.pool = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaxPoolSize(), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NumberedNameThreadFactory("qq-worker") );
	}
	
	public QQServer start() {
		logger.info("Starting work poller, good luck!\n{}", config);

		Duration freq = config.getPollingFrequency();
		poller.scheduleAtFixedRate(new WorkPoller(config, pool), 0, freq.toMillis(), TimeUnit.MILLISECONDS);
		return this;
	}
	
	public void stop() {
		try {
			logger.info("Stopping work poller...");
			poller.shutdownNow();
		} catch (Exception e) {
			logger.warn("Error stopping work poller, but we are shutting down anyhow. {}", e.getMessage());
		}
		
		try {
			logger.info("Stopping worker thread pool...");
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.warn("Error stopping worker thread pool, but we are shutting down anyhow. {}", e.getMessage());
		}
	}
}
