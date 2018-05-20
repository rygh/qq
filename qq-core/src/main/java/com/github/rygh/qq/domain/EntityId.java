package com.github.rygh.qq.domain;

public class EntityId {
	private Object entityId;
	private Class<?> entityType;
	
	public EntityId(Object entityId, String entityType) {
		this.entityId = entityId;
		try {
			this.entityType = Class.forName(entityType);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unknown class " + entityType);
		}
	}
	
	public EntityId(Object entityId, Class<?> entityType) {
		this.entityId = entityId;
		this.entityType = entityType;
	}

	public Object getEntityId() {
		return entityId;
	}
	
	public EntityId setEntityId(Object entityId) {
		this.entityId = entityId;
		return this;
	}
	
	public Class<?> getEntityType() {
		return entityType;
	}
	
	public EntityId setEntityType(Class<?> entityType) {
		this.entityType = entityType;
		return this;
	}

	@Override
	public String toString() {
		return "EntityId [entityId=" + entityId + ", entityType=" + entityType + "]";
	}
}
