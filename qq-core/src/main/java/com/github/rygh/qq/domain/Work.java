package com.github.rygh.qq.domain;

public class Work {
	private Long id;
	private Class<?> type;
	private Class<?> target;
	private WorkState state = WorkState.READY;
	
	
	private Work(Long id, Class<?> type) {
		this.id = id;
		this.type = type;
	}
	
	public static Work processEntity(Long id, Class<?> type) {
		return new Work(id, type);
	}
	
	public Work withService(Class<?> service) {
		this.target = service;
		return this;
	}
	
}
