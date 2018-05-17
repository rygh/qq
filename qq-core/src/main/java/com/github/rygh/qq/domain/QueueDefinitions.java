package com.github.rygh.qq.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueDefinitions {

	private static final Logger logger = LoggerFactory.getLogger(QueueDefinitions.class);
	
	private final Map<String, PoolDefinition> pools = new HashMap<>();
	private final Map<String, ConsumerDefintition> consumers = new HashMap<>();
	
	public QueueDefinitions(List<ConsumerDefintition> definitions, Set<PoolDefinition> definedPools, DefaultPoolDefinition defaultPool) {
		definitions.forEach(consumer -> {
			consumers.put(consumer.getName(), consumer);

			String poolName = consumer.getPool();
			if (! pools.containsKey(poolName)) {
				PoolDefinition pool = definedPools.stream()
					.filter(def -> def.getName().equals(poolName))
					.findFirst()
					.orElseGet(defaultPool.createDefault(consumer));
				
				pools.put(poolName, pool);
			}
		});
	}

	public Set<PoolDefinition> getPoolDefinitions() {
		return pools.values().stream().collect(Collectors.toSet());
	}
	
	public boolean isValid(String consumerName) {
		return consumers.containsKey(consumerName);
	}
	
	public Predicate<String> consumerIsDefined() {
		return consumerName -> isValid(consumerName);
	}
	
	public static class DefaultPoolDefinition {
		private int defaultCorePoolSize;
		private int defaultMaxPoolSize;

		public DefaultPoolDefinition(int defaultCorePoolSize, int defaultMaxPoolSize) {
			this.defaultCorePoolSize = defaultCorePoolSize;
			this.defaultMaxPoolSize = defaultMaxPoolSize;
		}
		
		public Supplier<PoolDefinition> createDefault(ConsumerDefintition consumer) {
			return () -> {
				logger.info("No pool definiton provided for {}, creating default pool for {}", consumer.getPool(), consumer);
				return new PoolDefinition(consumer.getPool(), defaultCorePoolSize, defaultMaxPoolSize);
			};
		}
	}
}
