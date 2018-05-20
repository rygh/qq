package com.github.rygh.qq;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.repositories.WorkRepository;

public class WorkPublisher {

	private final static Logger logger = LoggerFactory.getLogger(WorkPublisher.class);
	
	private final WorkRepository workRepository;
	private final EntityResolver entityResolver;
	
	public WorkPublisher(WorkRepository workRepository, EntityResolver entityResolver) {
		this.workRepository = workRepository;
		this.entityResolver = entityResolver;
	}

	public Work publish(Object payload, String consumer) {
		EntityId entity = entityResolver.extractEntityId(payload);
		
		Work work = workRepository.store(new Work(LocalDateTime.now(), entity, consumer));
		logger.debug("Publishing work {}", work);
		return work;
	}
	
}
