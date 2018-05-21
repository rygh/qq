package com.github.rygh.qq;

import java.util.Map;
import java.util.Set;

import com.github.rygh.qq.domain.ConsumerDefintition;
import com.github.rygh.qq.domain.ConsumerRegister;
import com.github.rygh.qq.domain.PoolDefinition;
import com.github.rygh.qq.error.ErrorHandler;
import com.github.rygh.qq.repositories.WorkRepository;

public interface QQContext {
	String getInstanceId();
	ConsumerRegister getConsumerRegister();
	WorkRepository getWorkRepository();
	TransactionWrapper getTransactionWrapper();
	Set<PoolDefinition> getWorkerPools();
	WorkPublisher getWorkPublisher();
	ErrorHandler getErrorHandler();
	Map<String, ConsumerDefintition> getConsumerDefinitions();
}
