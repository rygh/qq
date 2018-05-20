package com.github.rygh.qq.spring;

import java.util.Set;

import com.github.rygh.qq.QQContext;
import com.github.rygh.qq.TransactionalWorkerFactory;
import com.github.rygh.qq.WorkPublisher;
import com.github.rygh.qq.domain.ConsumerRegister;
import com.github.rygh.qq.domain.PoolDefinition;
import com.github.rygh.qq.repositories.WorkRepository;

class NullQQContext implements QQContext {

	@Override
	public String getInstanceId() {
		return null;
	}

	@Override
	public ConsumerRegister getConsumerRegister() {
		return null;
	}

	@Override
	public WorkRepository getWorkRepository() {
		return null;
	}

	@Override
	public TransactionalWorkerFactory getTransactionalWorkerFactory() {
		return null;
	}

	@Override
	public Set<PoolDefinition> getWorkerPools() {
		return null;
	}

	@Override
	public WorkPublisher getWorkPublisher() {
		return null;
	}

}
