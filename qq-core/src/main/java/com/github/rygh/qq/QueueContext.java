package com.github.rygh.qq;

public interface QueueContext {
	String getInstanceId();
	ConsumerRegister getConsumerRegister();
	WorkRepository getWorkRepository();
	TransactionalWorkerFactory getTransactionalWorkerFactory();
}
