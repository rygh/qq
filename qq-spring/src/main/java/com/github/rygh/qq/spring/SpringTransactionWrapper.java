package com.github.rygh.qq.spring;

import java.util.concurrent.Callable;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.TransactionWrapper;

public class SpringTransactionWrapper implements TransactionWrapper {

	private final TransactionTemplate transactionTemplate;

	public SpringTransactionWrapper(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = createTransactionTemplate(transactionManager);
	}
	
	private TransactionTemplate createTransactionTemplate(PlatformTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate;
	}
	
	public void doInTransaction(Runnable runnable) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus tx) {
				runnable.run();
			}
		});
	}
	
	public <T> T doInTransaction(Callable<T> callable) {
		return transactionTemplate.execute(new TransactionCallback<T>() {
			@Override
			public T doInTransaction(TransactionStatus tx) {
				try {
					return callable.call();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
