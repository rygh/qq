package com.github.rygh.qq;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;

public class UnitOfWork {

	private final static Logger logger = LoggerFactory.getLogger(UnitOfWork.class);
	
	private final WorkRepository repository;
	private final ConsumerRegister consumers;
	private final Work work;
	
	public UnitOfWork(QueueContext context, Work work) {
		this.work = work;
		this.repository = context.getWorkRepository();
		this.consumers = context.getConsumerRegister();
	}
	
	/**
	 * Should return something, this is kind of scriptish
	 */
	public void doWork() {
		// Attempt to lock work-item -> If not available some other consumer got it, exit!
		// Item remains locked for the duration of the job, this state is currently invisible to the outside!
		Optional<Work> lockedItem = repository.getByIdWithLock(work.getId());
		if (!lockedItem.isPresent()) {
			logger.debug("Failed to aquire lock for {}, moving along", work);
			return;
		}
		
		Work lockedWork = lockedItem.get();
		
		lockedWork.setStartedTime(LocalDateTime.now());
		
		// Locate consumer and provide work - consumer must load entity and perform processing
		consumers.getConsumerFor(work).accept(lockedWork.asEntityId());
		
		lockedWork.setState(WorkState.COMPLETED).setCompletedTime(LocalDateTime.now());
		repository.update(lockedWork); // DONE
	}
	
	public void handleError() {
		repository.update(work.setState(WorkState.FAILED)); // ERROR
	}
	
}
