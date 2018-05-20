package com.github.rygh.qq;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.PoolDefinition;
import com.github.rygh.qq.repositories.WorkRepository;

public class WorkPoller implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkPoller.class);
	
	private final WorkRepository workRepository;
	private final ThreadPoolExecutor pool;
	private final TransactionalWorkerFactory workerFactory;
	private final QQContext context;
	private final String name;
	
	public WorkPoller(QQContext context, PoolDefinition definition) {
		this.context = context;
		this.workRepository = context.getWorkRepository();
		this.workerFactory = context.getTransactionalWorkerFactory();
		this.pool = definition.createThreadPool();
		this.name = definition.getName();
	}

	private boolean poolIsShuttingDown() {
		return pool.isShutdown() || pool.isTerminated() || pool.isTerminating();
	}
	
	@Override
	public void run() {
		if (poolIsShuttingDown()) {
			logger.info("Worker thread pool is terminating, skipping poll for new work!");
			return;
		}
		
		if (pool.getQueue().isEmpty()) {
			int approxFreeThreads = pool.getMaximumPoolSize() - pool.getActiveCount();
			logger.debug("Queue is empty and pool {} got {} free threads, search for new work", name, approxFreeThreads);
			
			workRepository.claimNextReadyForPool(approxFreeThreads, name)
				.map(work -> new UnitOfWork(context, work))
				.map(workerFactory::createTransactionalWorkerFor)
				.forEach(pool::execute); // TODO: Wrapper around pool?
		}
	}
	
	public void stop() {
		try {
			logger.info("Stopping worker thread pool {}", name);
			pool.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.warn("Error stopping worker thread pool {}, but we are shutting down anyhow. {}", name, e.getMessage());
		}
	}
}

