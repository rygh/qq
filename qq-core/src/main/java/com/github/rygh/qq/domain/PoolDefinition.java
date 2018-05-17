package com.github.rygh.qq.domain;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.github.rygh.qq.util.NumberedNameThreadFactory;

public class PoolDefinition {

	private final String name;
	private final int corePoolSize;
	private final int maxPoolSize;

	public PoolDefinition(String name, int corePoolSize, int maxPoolSize) {
		this.name = name;
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
	}

	public ThreadPoolExecutor createThreadPool() {
		ThreadFactory threadFactory = new NumberedNameThreadFactory("qq-" + name);
		RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.AbortPolicy();
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory, rejectedHandler);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "PoolDefinition [name=" + name + ", corePoolSize=" + corePoolSize + ", maxPoolSize=" + maxPoolSize + "]";
	}
	
}
