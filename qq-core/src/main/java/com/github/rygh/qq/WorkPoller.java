package com.github.rygh.qq;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkPoller implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkPoller.class);
	
	private final WorkRepository workRepository;
	private final ThreadPoolExecutor pool;
	private final TransactionalWorkerFactory workerFactory;
	private final QueueContext context;
	
	public WorkPoller(QueueContext context, ThreadPoolExecutor pool) {
		this.context = context;
		this.workRepository = context.getWorkRepository();
		this.pool = pool;
		this.workerFactory = context.getTransactionalWorkerFactory();
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
			logger.debug("Queue is empty and pool got {} free threads, search for new work", approxFreeThreads);
			
			workRepository.claimNextReady(approxFreeThreads)
				.map(work -> new UnitOfWork(context, work))
				.map(workerFactory::createTransactionalWorkerFor)
				.forEach(pool::execute); // TODO: Wrapper around pool?
		}
	}
}

