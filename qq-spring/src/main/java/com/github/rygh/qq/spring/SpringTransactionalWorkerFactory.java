package com.github.rygh.qq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.TransactionalWorkerFactory;
import com.github.rygh.qq.UnitOfWork;

public class SpringTransactionalWorkerFactory implements TransactionalWorkerFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(SpringTransactionalWorkerFactory.class);
	
	private final TransactionTemplate transactionTemplate;
	 
	public SpringTransactionalWorkerFactory(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	class TransactionalWorker implements Runnable {

		private UnitOfWork work;
		
		public TransactionalWorker(UnitOfWork work) { // Replace with work, holds repository, consumers, work
			this.work = work;
		}

		@Override
		public void run() {
			logger.debug("Executing work {}", work);
			try {
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						work.doWork();
					}
				});
				
			} catch (Throwable t) {
				
				logger.error("Horrible error while processing " + work, t);
				// Failure, work transaction was rolled back, update state 
				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus status) {
						work.handleError();
					}
				});
			}
		}
	}

    @Override
	public Runnable createTransactionalWorkerFor(UnitOfWork work) {
		return new TransactionalWorker(work);
	}
	
}
