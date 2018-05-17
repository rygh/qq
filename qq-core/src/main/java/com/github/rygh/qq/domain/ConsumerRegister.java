package com.github.rygh.qq.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public void verifyConsumers(QueueDefinitions definitions) {
	 	Set<String> undefinedConsumers = getRegisteredConsumers().stream()
			.filter(definitions.consumerIsDefined().negate())
			.collect(Collectors.toSet());
		
	 	if (! undefinedConsumers.isEmpty()) {
	 		throw new IllegalStateException("Undefined consumers found in the code " + undefinedConsumers.toString());
	 	}
	}
	
	@Override
	public String toString() {
		return "Active consumers: " + consumers.keySet();
	}
}
