package com.github.rygh.qq.domain;

public class EntityId {
	private String entityId;
	private String entityType;
	
	public EntityId(String entityId, String entityType) {
		this.entityId = entityId;
		this.entityType = entityType;
	}
	
	public EntityId(String entityId, Class<?> entityType) {
		this.entityId = entityId;
		this.entityType = entityType.getName();
	}

	public String getEntityId() {
		return entityId;
	}
	
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getEntityType() {
		return entityType;
	}
	
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
}
