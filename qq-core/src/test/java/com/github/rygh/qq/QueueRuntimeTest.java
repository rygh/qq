package com.github.rygh.qq;

import java.util.concurrent.TimeUnit;

public class QueueRuntimeTest {

	public static void main(String ...strings ) {
	
		QueueRuntime runtime = new QueueRuntime(QueueConfig.withDefaults());
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(10);
					runtime.stop();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}, "assassin").start();
		
		
		runtime.start();
	}
}
