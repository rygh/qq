package com.github.rygh.qq;

import java.util.Objects;

public class QQContextHolder {

	private static QueueContext context;
	
	public static QueueContext getContext() {
		return Objects.requireNonNull(context, "QueueContext is null, accessed too early?");
	}
	
	public static void setContext(QueueContext context) {
		if (context != null) {
			throw new IllegalStateException("QueueContext already set");
		}
		
		QQContextHolder.context = context;
	}
	
}
