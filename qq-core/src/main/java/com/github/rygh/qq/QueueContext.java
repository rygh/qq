package com.github.rygh.qq;

import java.util.HashMap;
import java.util.Map;

public class QueueContext {

	private Map<String, String> consumers = new HashMap<String, String>();
	
	
	
	public QueueContext(QueueConfig config) {
		
	}
	
	
	
	private void initializeConsumers() {
		// Using populator from Config
	}
	
	private void initializeCallers() {
		// ? -- skip for now -- first relevant when we get proxies
		
		// 1: QueueProducer that must be injected into each using service
		// 2: QueueProducer.execute(QueueTarget, Entity);
		// 3: Context.lookup(QueueTarget) => store work-item
		
		// - step 2 => Proxy(contains QueueProducer)
		
	}
	
}
