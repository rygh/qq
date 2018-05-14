package com.github.rygh.qq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;

public class ConsumerRegister {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerRegister.class);
	
	private final Map<String, Consumer<EntityId>> consumers = new HashMap<>();
	
	public ConsumerRegister register(String name, Consumer<EntityId> implementation) {
		if (consumers.containsKey(name)) {
			throw new IllegalArgumentException(name + " already bound to consumer, " + implementation.getClass());
		}
		consumers.put(name, implementation);
		logger.info("Registered consumer {} with {}", name, implementation);
		
		return this;
	}
	
	public Consumer<EntityId> getConsumerFor(Work work) {
		return consumers.getOrDefault(work.getConsumer(), consumerNotFound(work));
	}
	
	private Consumer<EntityId> consumerNotFound(Work work) {
		return (entity) -> {
			throw new IllegalStateException("No consumer registered for " + work.getConsumer() + ", unable to process " + work);
		};
	}
	
	public Set<String> getRegisteredConsumers() {
		return Collections.unmodifiableSet(consumers.keySet());
	}
	
	@Override
	public String toString() {
		return "Active consumers: " + consumers.keySet();
	}
}
