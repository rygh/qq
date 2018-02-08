package com.github.rygh.qq;

import java.util.concurrent.ThreadPoolExecutor;

import com.github.rygh.qq.domain.Work;

public class WorkPoller implements Runnable {
	
	private WorkRepository workRepository;
	private ThreadPoolExecutor worker;
	
	public WorkPoller(WorkRepository workRepository, ThreadPoolExecutor worker) {
		this.workRepository = workRepository;
		this.worker = worker;
	}

	@Override
	public void run() {
		
		System.out.println(Thread.currentThread().getName() + " - Polling");

		if (worker.isShutdown() || worker.isTerminated() || worker.isTerminating()) {
			return;
		}
		
		if (worker.getQueue().isEmpty()) {
			for (Work work : workRepository.findWork()) {
				worker.execute(new ExecutableWork(work));
			}	
		}
	}
}
