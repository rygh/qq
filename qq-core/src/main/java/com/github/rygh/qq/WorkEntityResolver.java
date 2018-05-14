package com.github.rygh.qq;

import com.github.rygh.qq.domain.EntityId;

public interface WorkEntityResolver {
	EntityId extractEntityId(Object obj);
	<T> T loadEntity(EntityId id);
}
