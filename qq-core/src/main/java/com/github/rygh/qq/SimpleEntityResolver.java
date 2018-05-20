package com.github.rygh.qq;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.rygh.qq.domain.EntityId;

public class SimpleEntityResolver implements EntityResolver {

	private final Map<Class<?>, Function<Object, EntityId>> extractors = new HashMap<>();
	private final Map<Class<?>, Function<EntityId, Object>> suppliers = new HashMap<>();

	public SimpleEntityResolver registerEntityIdExtractor(Class<?> clazz, Function<Object, EntityId> function) {
		extractors.put(clazz, function);
		return this;
	}
	
	public SimpleEntityResolver registerEntityLoader(Class<?> clazz, Function<EntityId, Object> loader) {
		suppliers.put(clazz, loader);
		return this;
	}
	
	@Override
	public EntityId extractEntityId(Object obj) {
		Class<?> clazz = obj.getClass();
		if (! extractors.containsKey(clazz)) {
			throw new IllegalArgumentException("Unable to extract EntityId for " + obj);
		}
		
		return extractors.get(obj.getClass()).apply(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T loadEntity(EntityId id) {
		if (! suppliers.containsKey(id.getEntityType())) {
			throw new IllegalArgumentException("Unable to load entity for " + id);
		}
		
		return (T) suppliers.get(id.getEntityType());
	}
}
