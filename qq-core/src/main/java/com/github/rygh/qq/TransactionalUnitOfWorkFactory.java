package com.github.rygh.qq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionalUnitOfWorkFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(TransactionalUnitOfWorkFactory.class);
	
	private final TransactionWrapper transactionManager;
	 
	public TransactionalUnitOfWorkFactory(QQContext context) {
		this.transactionManager = context.getTransactionWrapper();
	}
	
	class TransactionalWorker implements Runnable {

		private UnitOfWork work;
		
		public TransactionalWorker(UnitOfWork work) { 
			this.work = work;
		}

		@Override
		public void run() {
			logger.debug("Executing work {}", work);
			try {
				transactionManager.doInTransaction(work::doWork);
			} catch (Throwable t) {
				
				logger.error("Horrible error while processing " + work, t);
				// Failure, work transaction was rolled back, update state 
				transactionManager.doInTransaction(work::handleError);
			}
		}
	}

	public Runnable createTransactionalWorkerFor(UnitOfWork work) {
		return new TransactionalWorker(work);
	}
	
}
