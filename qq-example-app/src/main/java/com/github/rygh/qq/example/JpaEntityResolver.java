package com.github.rygh.qq.example;

import javax.persistence.EntityManager;

import com.github.rygh.qq.EntityResolver;
import com.github.rygh.qq.domain.EntityId;

@SuppressWarnings("unchecked")
public class JpaEntityResolver implements EntityResolver {

	private EntityManager em;
	
	public JpaEntityResolver(EntityManager entityManager) {
		this.em = entityManager;
	}

	@Override
	public EntityId extractEntityId(Object obj) {
		Object identifier = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj);
		Class<?> clazz = obj.getClass(); // Potential proxy trouble?
		return new EntityId(identifier, clazz);
	}

	@Override
	public <T> T loadEntity(EntityId id) {
		return (T) em.find(id.getEntityType(), id.getEntityId());
	}
}
