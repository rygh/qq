package com.github.rygh.qq.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberedNameThreadFactory implements ThreadFactory {

	private String base;
	private AtomicInteger counter = new AtomicInteger(0);
	
	public NumberedNameThreadFactory(String base) {
		this.base = base;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, base + "-" + counter.incrementAndGet());
	}

}
