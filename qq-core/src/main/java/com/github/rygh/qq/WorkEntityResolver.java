package com.github.rygh.qq;

import com.github.rygh.qq.domain.EntityId;

public interface WorkEntityResolver {
	EntityId resolve(Object obj);
}
