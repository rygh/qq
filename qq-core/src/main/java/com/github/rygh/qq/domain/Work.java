package com.github.rygh.qq.domain;

import java.time.LocalDateTime;

public class Work {
	private Long id;
	private LocalDateTime createdTime;
	private LocalDateTime startedTime;
	private LocalDateTime completedTime;
	private String consumer;
	private EntityId entityId;
	private WorkState state = WorkState.READY;
	
	private int executionCount = 0;
	private String errorMessage;
	
	private int version = 1;
	
	public Work(LocalDateTime createdTime, EntityId entityId, String consumer) {
		this.createdTime = createdTime;
		this.entityId = entityId;
		this.consumer = consumer;
	}

	public Work(Long id, LocalDateTime createdTime, EntityId entityId, String consumer) {
		this(createdTime, entityId, consumer);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public Work setId(Long id) {
		this.id = id;
		return this;
	}
	
	public EntityId getEntityId() {
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

	public boolean is(WorkState requestedState) {
		return this.state == requestedState;
	}
	
	public int getExecutionCount() {
		return executionCount;
	}

	public Work setExecutionCount(int executionCount) {
		this.executionCount = executionCount;
		return this;
	}
	
	public Work incrementExecutionCount() {
		this.executionCount++;
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Work setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	@Override
	public String toString() {
		return "Work [id=" + id 
				+ ", entityId=" + entityId 
				+ ", consumer=" + consumer
				+ ", state=" + state 
				+ ", version=" + version
				+ "]";
	}

}
