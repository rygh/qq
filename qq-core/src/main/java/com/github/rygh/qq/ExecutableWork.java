package com.github.rygh.qq;

import com.github.rygh.qq.domain.Work;

class ExecutableWork implements Runnable {
	
	// Work
	// TransactionManager
	// WorkRepository
	// Actual Worker Delegate
	// DataSource
	
	public ExecutableWork(Work work) {
	}

	@Override
	public void run() {

		// Set work as processing + commit
		
		// Run job in proper queue in transaction
		System.out.println(Thread.currentThread().getName() + " - Doing some work");
	
		// Set work as failed or done + commit
	}
}