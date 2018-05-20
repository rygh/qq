package com.github.rygh.qq;

import java.util.concurrent.Callable;

public interface TransactionWrapper {
	void doInTransaction(Runnable runnable);
	<T> T doInTransaction(Callable<T> callable);
}
