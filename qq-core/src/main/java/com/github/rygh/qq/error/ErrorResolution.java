package com.github.rygh.qq.error;

import java.util.function.Function;

public class ErrorResolution {
	private boolean shouldRetry = false;
	private boolean shouldLogError = true;
	private Function<Throwable, String > errorMapper = (t) -> t.getMessage();

	public boolean shouldRetry() {
		return shouldRetry;
	}
	
	public ErrorResolution setShouldRetry(boolean shouldRetry) {
		this.shouldRetry = shouldRetry;
		return this;
	}
	
	public boolean shouldLogError() {
		return shouldLogError;
	}
	
	public ErrorResolution setShouldLogError(boolean shouldLogError) {
		this.shouldLogError = shouldLogError;
		return this;
	}
	
	public String formatErrorMessage(Throwable t) {
		return errorMapper.apply(t);
	}
	
	public ErrorResolution setErrorMapper(Function<Throwable, String> errorMapper) {
		this.errorMapper = errorMapper;
		return this;
	}
}