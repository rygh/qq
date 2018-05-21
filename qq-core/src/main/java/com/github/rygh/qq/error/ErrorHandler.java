package com.github.rygh.qq.error;

import com.github.rygh.qq.domain.ConsumerDefintition;

public interface ErrorHandler {
	ErrorResolution handle(Throwable ex, ConsumerDefintition consumer, int executionCount);
}