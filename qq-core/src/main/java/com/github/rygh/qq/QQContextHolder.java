package com.github.rygh.qq;

import java.util.Objects;

public class QQContextHolder {

	private static QQContext context;
	
	public static QQContext getContext() {
		return Objects.requireNonNull(context, "QueueContext is null, accessed too early?");
	}
	
	public static void setContext(QQContext context) {
		QQContextHolder.context = context;
	}
	
}
