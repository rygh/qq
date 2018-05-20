package com.github.rygh.qq.example.job;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.rygh.qq.domain.WorkState;

@Entity
@Table(name = "work")
public class Job {

	@Id
	private Long id;
	private LocalDateTime createdTime;
	private LocalDateTime startedTime;
	private LocalDateTime completedTime;
	private String consumer;
	private String entityId;
	private String entityClass;
	@Enumerated(EnumType.STRING)
	private WorkState state;
	
	public Long getId() {
		return id;
	}
	
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}
	
	public LocalDateTime getStartedTime() {
		return startedTime;
	}
	
	public LocalDateTime getCompletedTime() {
		return completedTime;
	}
	
	public String getConsumer() {
		return consumer;
	}
	
	public String getEntityId() {
		return entityId;
	}
	
	public String getEntityClass() {
		return entityClass;
	}
	
	public WorkState getState() {
		return state;
	}
	
}
