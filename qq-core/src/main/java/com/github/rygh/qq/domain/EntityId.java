package com.github.rygh.qq.domain;

import java.util.UUID;

public class EntityId {
	private Object entityId;
	private Class<?> entityType;
	private Class<?> idType;
	
	public EntityId(Class<?> entityClass) {
		this.entityType = entityClass;
	}
	
	public EntityId setEntityId(String id, Class<?> idClass) {
		this.idType = idClass;
		if (idClass.equals(UUID.class)) {
			this.entityId = UUID.fromString(id);
		} else if (idClass.equals(String.class)) {
			this.entityId = id;
		} else if (idClass.equals(Long.class)) {
			this.entityId = Long.valueOf(id);
		} else if (idClass.equals(Integer.class)) {
			this.entityId = Integer.valueOf(id);
		} else {
			throw new IllegalArgumentException("ID type " + idClass + " not supported!");
		}
		
		return this;
	}

	public EntityId setEntityId(Object id) {
		this.idType = id.getClass();
		this.entityId = id;
		
		if (!idType.equals(UUID.class) 
				&& !idType.equals(String.class)
				&& !idType.equals(Long.class)
				&& !idType.equals(Integer.class))  {
			throw new IllegalArgumentException("ID type " + idType + " not supported!");
		}
		return this;
	}	
	
	public Object getEntityId() {
		return entityId;
	}
	
	public Class<?> getEntityType() {
		return entityType;
	}
	
	public Class<?> getIdType() {
		return idType;
	}
	
	@Override
	public String toString() {
		return "EntityId [entityId=" + entityId 
			+ ", entityType=" + entityType 
			+ ", idType=" + idType + "]";
	}
}
