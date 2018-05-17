package com.github.rygh.qq;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.util.NamedThreadFactory;

public class QQServer {

	private static final Logger logger = LoggerFactory.getLogger(QQServer.class);
	
	private final ScheduledExecutorService scheduler;
	private final QueueConfig config;
	private final Set<WorkPoller> workPollers = new HashSet<>();
	
	public QQServer(QueueConfig config) {
		this.config = config;
		this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("qq-poller"));
	}
	
	public QueueContext start() {
		logger.info("Starting queue, good luck!\n{}", config);

		QueueContext context = config.buildQueueContext();

		Duration freq = config.getPollingFrequency();
		context.getWorkerPools().forEach(definition -> {
			logger.info("Starting work poller using {}", definition);
			WorkPoller poller = new WorkPoller(context, definition);
			scheduler.scheduleAtFixedRate(poller, 0, freq.toMillis(), TimeUnit.MILLISECONDS);
			workPollers.add(poller);
		});
		
		return context;
	}
	
	public void stop() {
		try {
			logger.info("Stopping work poller...");
			scheduler.shutdownNow();
		} catch (Exception e) {
			logger.warn("Error stopping work poller, but we are shutting down anyhow. {}", e.getMessage());
		}

		workPollers.forEach(WorkPoller::stop);
	}
}
