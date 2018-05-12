package com.github.rygh.qq;

public interface QueueContext {
	ConsumerRegister getConsumerRegister();
	WorkRepository getWorkRepository();
	TransactionalWorkerFactory getTransactionalWorkerFactory();
}
