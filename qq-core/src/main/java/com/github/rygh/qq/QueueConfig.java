package com.github.rygh.qq;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.domain.Work;

public class QueueConfig {

	private WorkRepository workRepository;
	private PlatformTransactionManager platformTransactionManager;
	
	private String instanceId = UUID.randomUUID().toString();
	private TimeZone displayTimeZone = TimeZone.getDefault();
	
	private QueueConfig() {
	}

	public static QueueConfig withDefaults() {
		return new QueueConfig();
	}

	// Nope! Config should begat context
	public Supplier<Map<String, Consumer<Work>>> getQueueSource() {
		return () -> {
			return Collections.EMPTY_MAP;
		};
	}
	
	public PlatformTransactionManager getTransactionManager() {
		return platformTransactionManager;
	}
	
	public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
		this.platformTransactionManager = platformTransactionManager;
	}
	
	public void setWorkRepository(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}
	
	public TransactionTemplate createTransactionTemplate() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(getTransactionManager());
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate;
	}
	
	public WorkRepository getWorkRepository() {
		return workRepository; // TODO: Default inmemory repository
	}
	
	public int getCorePoolSize() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	public int getMaxPoolSize() {
		return getCorePoolSize() * 5;
	}
	
	public Duration getPollingFrequency() {
		return Duration.of(1L, ChronoUnit.SECONDS);
	}

	@Override
	public String toString() {
		return "Current QueueConfig\n"
				+ "* WorkRepository......" + getWorkRepository().getClass() + "\n"
				+ "* MaxPoolSize........." + getMaxPoolSize() + "\n"
				+ "* CorePoolSize........" + getCorePoolSize() + "\n"
				+ "* PollingFrequency...." + getPollingFrequency() + "\n";
		
	}
}
