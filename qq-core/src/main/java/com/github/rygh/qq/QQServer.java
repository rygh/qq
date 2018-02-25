package com.github.rygh.qq;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.github.rygh.qq.util.NamedThreadFactory;
import com.github.rygh.qq.util.NumberedNameThreadFactory;

/**
 * 
* Core
	- QueueManager
	- Interface: WorkItemRepository, default: plain jdbc
	- Interface: QueueConfiguration, default: manual update of map
	- Interface: Executor, default: execute by interface from queueConfiguration. 
	- Interface: EntityLoader, default: load item + entity using plain jdbc
	- Interface: EntityPersister


* Config -> start(config) => CreateContext (queues, count, naming, consumers)

	Poll-items
		For item
			Find Queue
			Get Thread
			Execute Queue with item
				tx {
					Load referred item
					Do magic
					Persist data
				}
			Update item state

			EntityLoader kan kjøres som et pre-steg

* Spring
	- SpringQueueConfiguration, scan for 
	- SpringInjector, inject proxy

* Hibernate
	- EntityLoader / Persister

* Default-Dist
	- Setup with spring & hibernate
0 New

 *
 */
public class QQServer {

	private ScheduledExecutorService poller;
	private ThreadPoolExecutor worker;
	
	private QueueConfig config;
	
	public QQServer(QueueConfig config) {
		this.config = config;
		
		this.poller = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("qq-poller"));
		this.worker = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaxPoolSize(), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NumberedNameThreadFactory("qq-worker") );
	}
	
	public void start() {
		System.out.println("Starting poller, good luck!");
		System.out.println(config);
		
		Duration freq = config.getPollingFrequency();
		poller.scheduleAtFixedRate(new WorkPoller(config.getWorkRepository(), worker), 0, freq.toMillis(), TimeUnit.MILLISECONDS);
	}
	
	public void stop() {
		System.out.println("Stopping");
		poller.shutdown();
		worker.shutdown();
	}
}
