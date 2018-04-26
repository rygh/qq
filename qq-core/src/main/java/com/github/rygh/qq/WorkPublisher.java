package com.github.rygh.qq;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.Work;

public class WorkPublisher {

	private final static Logger logger = LoggerFactory.getLogger(WorkPublisher.class);
	
	private final WorkRepository workRepository;
	
	public WorkPublisher(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}

	public Work publish(Class<?> entityType, String entityId, String consumer) {
		Work work = workRepository.store(new Work(LocalDateTime.now(), entityType.getName(), entityId, consumer));
		logger.debug("Publishing work {}", work);
		return work;
	}
	
}
