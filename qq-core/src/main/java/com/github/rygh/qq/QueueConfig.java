package com.github.rygh.qq;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Supplier;

public class QueueConfig {

	private WorkRepository workRepository;
	
	private String instanceId = UUID.randomUUID().toString();
	private TimeZone displayTimeZone = TimeZone.getDefault();
	private Optional<Integer> poolSize = Optional.empty();
	private Supplier<ConsumerRegister> consumerSupplier = () -> new ConsumerRegister();
	private TransactionalWorkerFactory transactionalWorkerFactory;
	
	private QueueConfig() {
	}

	public static QueueConfig withDefaults() {
		return new QueueConfig();
	}

    public void setConsumerRegister(ConsumerRegister register) {
    	this.consumerSupplier = () -> register;
    }
    
    public void setConsumerRegisterSupplier(Supplier<ConsumerRegister> consumerSupplier) {
		this.consumerSupplier = consumerSupplier;
	}
    
	public void setWorkRepository(WorkRepository workRepository) {
		this.workRepository = workRepository;
	}
	
	public WorkRepository getWorkRepository() {
		return workRepository; // TODO: Default inmemory repository
	}
	
	public QueueConfig setCorePoolSize(int size) {
		poolSize = Optional.of(size);
		return this;
	}
	
	public int getCorePoolSize() {
		return poolSize.orElseGet(() -> Runtime.getRuntime().availableProcessors());
	}
	
	public int getMaxPoolSize() {
		return getCorePoolSize() * 5;
	}
	
	public Duration getPollingFrequency() {
		return Duration.of(1L, ChronoUnit.SECONDS);
	}
	
	public void setTransactionalWorkerFactory(TransactionalWorkerFactory transactionalWorkerFactory) {
		this.transactionalWorkerFactory = transactionalWorkerFactory;
	}

	/**
	 * Initialize required components from config and return immutable context
	 */
	public QueueContext buildQueueContext() {
		final ConsumerRegister register = requireNonNull(consumerSupplier.get(), "Consumer register or supplier must be set in config");
		final WorkRepository repository = requireNonNull(workRepository, "Work Repoistory must be set in the config");
		final TransactionalWorkerFactory workerFactory = requireNonNull(transactionalWorkerFactory, "Transactional WorkerFactory must be set in config");
		
		return new QueueContext() {
			@Override
			public ConsumerRegister getConsumerRegister() {
				return register;
			}
			
			@Override
			public WorkRepository getWorkRepository() {
				return repository;
			}
			
			@Override
			public TransactionalWorkerFactory getTransactionalWorkerFactory() {
				return workerFactory;
			}
		};
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
