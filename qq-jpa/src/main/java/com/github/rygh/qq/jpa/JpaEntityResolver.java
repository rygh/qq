package com.github.rygh.qq.jpa;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.EntityResolver;
import com.github.rygh.qq.domain.EntityId;

@SuppressWarnings("unchecked")
public class JpaEntityResolver implements EntityResolver {

	private static final Logger logger = LoggerFactory.getLogger(JpaEntityResolver.class);
	
	private EntityManager em;
	
	public JpaEntityResolver(EntityManager entityManager) {
		this.em = entityManager;
	}

	@Override
	public EntityId extractEntityId(Object obj) {
		Object identifier = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj);
		Class<?> clazz = obj.getClass(); // Potential proxy trouble?
		EntityId entityId = new EntityId(clazz).setEntityId(identifier);
		
		logger.debug("Extracted {} for {}", entityId, obj);
		return entityId;
	}

	@Override
	public <T> T loadEntity(EntityId id) {
		T entity = (T) em.find(id.getEntityType(), id.getEntityId());
		
		logger.debug("Loaded {} for {}", entity, id);
		return entity;
	}
}
