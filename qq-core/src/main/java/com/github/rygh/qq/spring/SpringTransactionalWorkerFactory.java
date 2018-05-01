package com.github.rygh.qq.spring;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.QueueConfig;
import com.github.rygh.qq.WorkRepository;
import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;

public class SpringTransactionalWorkerFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(SpringTransactionalWorkerFactory.class);
	
	private final TransactionTemplate transactionTemplate;
	private final WorkRepository workRepository;
	private final Map<String, Consumer<Work>> consumers;
	
	public SpringTransactionalWorkerFactory(QueueConfig config) {
		this.consumers = config.getQueueSource().get();
		this.workRepository = config.getWorkRepository();
		this.transactionTemplate = config.createTransactionTemplate();
	}
	
	class TransactionalWorker implements Runnable {

		private Work work;
		
		public TransactionalWorker(Work work) {
			this.work = work;
		}

		@Override
		public void run() {
			
			logger.debug("Executing work {}", work);
			
			if (work.getState() != WorkState.PROCESSING) {
				throw new IllegalStateException("Work is not ready " + work);
			}
			
			try {
				// Do the actual work
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						
						// Attempt to lock work-item -> If not available some other consumer got it, exit!
						// Item remains locked for the duration of the job, this state is currently invisible to the outside!
						Optional<Work> lockedItem = workRepository.getByIdWithLock(work.getId());
						if (!lockedItem.isPresent()) {
							logger.debug("Failed to aquire lock for {}, moving along", work);
							return;
						}
						
						Work lockedWork = lockedItem.get();
						
						lockedWork.setStartedTime(LocalDateTime.now());
						
						// Locate consumer and provide work - consumer must load entity and perform processing
						// We can plug in a consumer type that uses Spring to get beans and JPA to load entities
						consumers.getOrDefault(work.getClass(), new DefaultConsumer())
							.accept(lockedWork);
						
						lockedWork.setState(WorkState.COMPLETED).setCompletedTime(LocalDateTime.now());
						workRepository.update(lockedWork); // DONE
					}
				});
				
			} catch (Throwable t) {
				
				logger.error("Horrible error while processing " + work, t);
				
				// Failure, work transaction was rolled back, update state 
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						workRepository.update(work.setState(WorkState.FAILED)); // ERROR
					}
				});
			}
		}
	}

	public Runnable createTransactionalWorkerFor(Work work) {
		return new TransactionalWorker(work);
	}
	
	class DefaultConsumer implements Consumer<Work> {
		@Override
		public void accept(Work work) {
			
			System.err.println("Using default consumer, doing some random stuff...");
			try {
				TimeUnit.MILLISECONDS.sleep(150);
			} catch (InterruptedException e) {
			}
			System.err.println("DONE with " + work.getId());
			
			//throw new RuntimeException("No consumer for " +t);
		}
	}
}
