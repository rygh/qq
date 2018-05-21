package com.github.rygh.qq;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.ConsumerDefintition;
import com.github.rygh.qq.domain.ConsumerRegister;
import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;
import com.github.rygh.qq.error.ErrorHandler;
import com.github.rygh.qq.error.ErrorResolution;
import com.github.rygh.qq.repositories.WorkRepository;

public class UnitOfWork implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(UnitOfWork.class);
	
	private final TransactionWrapper transactionWrapper;
	private final WorkRepository repository;
	private final ConsumerRegister consumers;
	private final ErrorHandler errorHandler;
	private final Map<String, ConsumerDefintition> consumerDefinitions;

	private final Long workId;
	
	public UnitOfWork(QQContext context, Work work) {
		this.workId = work.getId();
		this.repository = context.getWorkRepository();
		this.consumers = context.getConsumerRegister();
		this.transactionWrapper = context.getTransactionWrapper();
		this.errorHandler = context.getErrorHandler();
		this.consumerDefinitions = context.getConsumerDefinitions();
	}
	
	@Override
	public void run() {
		try {
			transactionWrapper.doInTransaction(this::attemptTask);
		} catch (Throwable t) {
			transactionWrapper.doInTransaction(() -> this.handleError(t));
		}
	}
	
	/**
	 * Should return something, this is kind of scriptish
	 */
	private void attemptTask() {
		logger.debug("Executing work {}", workId);
		// Attempt to lock work-item -> If not available some other consumer got it, exit!
		// Item remains locked for the duration of the job, this state is currently invisible to the outside!
		Optional<Work> lockedItem = repository.getByIdWithLock(workId);
		if (!lockedItem.isPresent()) {
			logger.debug("Failed to aquire lock for work with id {}, moving along", workId);
			return;
		}
		
		Work work = lockedItem.get();
		
		work.setStartedTime(LocalDateTime.now());
		
		// Locate consumer and provide work - consumer must load entity and perform processing
		consumers.getConsumerFor(work).accept(work.getEntityId());
		
		work.setState(WorkState.COMPLETED)
			.setCompletedTime(LocalDateTime.now())
			.incrementExecutionCount();
		
		repository.update(work);
	}
	
	private void handleError(Throwable ex) {
		
		Optional<Work> lockedItem = repository.getByIdWithLock(workId);
		if (!lockedItem.isPresent()) {
			logger.debug("Failed to aquire lock for work with id {}, moving along", workId);
			return;
		}
		
		Work work = lockedItem.get();
		work.incrementExecutionCount();
		
		logger.debug("Horrible things while processing {}", work);
		logger.debug("Exception", ex);
		
		ErrorResolution resolution = errorHandler.handle(ex, consumerDefinitions.get(work.getConsumer()), work.getExecutionCount());

		String message = resolution.formatErrorMessage(ex);
		if (resolution.shouldLogError()) {
			logger.error("Error while processing " + work + ", message " + message, ex);
		} 
		
		if (resolution.shouldRetry()) {
			logger.info("Will retry work after failure {}", work);
			repository.update(work.setState(WorkState.READY));
		} else {
			repository.update(work.setState(WorkState.FAILED).setErrorMessage(message));
		}
	}
}
