package com.github.rygh.qq.domain;

import java.time.LocalDateTime;

public class Work {
	private Long id;
	private LocalDateTime createdTime;
	private LocalDateTime startedTime;
	private LocalDateTime completedTime;
	private String entityType;
	private String entityId;
	private String consumer;
	private WorkState state = WorkState.READY;
	private int version = 1;
	
	public Work(LocalDateTime createdTime, String entityType, String entityId, String consumer) {
		this.createdTime = createdTime;
		this.entityType = entityType;
		this.entityId = entityId;
		this.consumer = consumer;
	}

	public Work(Long id, LocalDateTime createdTime, String entityType, String entityId, String consumer) {
		this(createdTime, entityType, entityId, consumer);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public Work setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getEntityType() {
		return entityType;
	}
	
	public String getEntityId() {
		return entityId;
	}
	
	public WorkState getState() {
		return state;
	}
	
	public Work setState(WorkState state) {
		this.state = state;
		return this;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public LocalDateTime getStartedTime() {
		return startedTime;
	}

	public Work setStartedTime(LocalDateTime startedTime) {
		this.startedTime = startedTime;
		return this;
	}

	public LocalDateTime getCompletedTime() {
		return completedTime;
	}

	public Work setCompletedTime(LocalDateTime completedTime) {
		this.completedTime = completedTime;
		return this;
	}

	public String getConsumer() {
		return consumer;
	}
	
	public int getVersion() {
		return version;
	}
	
	public Work setVersion(int version) {
		this.version = version;
		return this;
	}
	
	public int nextVersion() {
		return version + 1;
	}

	@Override
	public String toString() {
		return "Work [id=" + id 
				+ ", entityType=" + entityType 
				+ ", entityId=" + entityId 
				+ ", consumer=" + consumer
				+ ", state=" + state 
				+ "]";
	}
}
