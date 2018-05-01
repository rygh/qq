package com.github.rygh.qq;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.spring.SpringTransactionalWorkerFactory;

public class WorkPoller implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkPoller.class);
	
	private final WorkRepository workRepository;
	private final ThreadPoolExecutor pool;
	private final SpringTransactionalWorkerFactory workerFactory;
	
	public WorkPoller(QueueConfig config, ThreadPoolExecutor pool) {
		this.workRepository = config.getWorkRepository();
		this.pool = pool;
		this.workerFactory = new SpringTransactionalWorkerFactory(config);
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
				.map(workerFactory::createTransactionalWorkerFor)
				.forEach(pool::execute); // TODO: Wrapper around pool?
		}
	}
}


// TEST: Work is retrieved
// TEST: Executing work, correct states
// TEST: Error handling - need to set error
// TEST: Oppførsel: Arbeid hentes ned og konsumeres, tilstand endres
// TEST: Sette opp H2
// TEST: Rest service for å poste work


