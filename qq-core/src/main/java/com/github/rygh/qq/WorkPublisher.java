package com.github.rygh.qq;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;

public class WorkPublisher {

	private final static Logger logger = LoggerFactory.getLogger(WorkPublisher.class);
	
	private final WorkRepository workRepository;
	private final WorkEntityResolver entityResolver;
	
	public WorkPublisher(WorkRepository workRepository, WorkEntityResolver entityResolver) {
		this.workRepository = workRepository;
		this.entityResolver = entityResolver;
	}

	public Work publish(Object payload, String consumer) {
		EntityId entity = entityResolver.resolve(payload);
		
		Work work = workRepository.store(new Work(LocalDateTime.now(), entity.getEntityType(), entity.getEntityId(), consumer));
		logger.debug("Publishing work {}", work);
		return work;
	}
	
}
