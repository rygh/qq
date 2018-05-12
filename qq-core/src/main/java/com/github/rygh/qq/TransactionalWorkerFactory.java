package com.github.rygh.qq;

public interface TransactionalWorkerFactory {
	Runnable createTransactionalWorkerFor(UnitOfWork work);
}