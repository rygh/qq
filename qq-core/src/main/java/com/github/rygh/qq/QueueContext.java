package com.github.rygh.qq;

import java.util.Set;

import com.github.rygh.qq.domain.ConsumerRegister;
import com.github.rygh.qq.domain.PoolDefinition;
import com.github.rygh.qq.repositories.WorkRepository;

public interface QueueContext {
	String getInstanceId();
	ConsumerRegister getConsumerRegister();
	WorkRepository getWorkRepository();
	TransactionalWorkerFactory getTransactionalWorkerFactory();
	Set<PoolDefinition> getWorkerPools();
}
