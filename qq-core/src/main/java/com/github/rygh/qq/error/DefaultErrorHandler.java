package com.github.rygh.qq.error;

import com.github.rygh.qq.domain.ConsumerDefintition;

public class DefaultErrorHandler implements ErrorHandler {
	@Override
	public ErrorResolution handle(Throwable ex, ConsumerDefintition consumer, int executionCount) {
		return new ErrorResolution().setShouldLogError(true).setShouldRetry(false);
	}
}
