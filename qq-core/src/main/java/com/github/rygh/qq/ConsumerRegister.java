package com.github.rygh.qq;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.github.rygh.qq.domain.Work;

public class ConsumerRegister {

	private final Map<String, Consumer<Work>> consumers = new HashMap<>();
	private final Consumer<Work> fallbackConsumer;
	
	public ConsumerRegister() {
		this(ConsumerRegister::failForUnknownConsumer);
	}
	
	public ConsumerRegister(Consumer<Work> fallbackConsumer) {
		this.fallbackConsumer = fallbackConsumer;
	}
	
	public ConsumerRegister register(String name, Consumer<Work> implementation) {
		if (consumers.containsKey(name)) {
			throw new IllegalArgumentException(name + " already bound to consumer, " + implementation.getClass());
		}
		consumers.put(name, implementation);
		return this;
	}
	
	public Consumer<Work> getConsumerFor(Work work) {
		return consumers.getOrDefault(work.getConsumer(), fallbackConsumer);
	}
	
	private static void failForUnknownConsumer(Work work) {
		throw new IllegalArgumentException("No consumer registered for " + work);
	}
	
	@Override
	public String toString() {
		return "Active consumers: " + consumers.keySet();
	}
}
