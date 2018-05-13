package com.github.rygh.qq;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueConfig {

	private static final Logger logger = LoggerFactory.getLogger(QueueConfig.class);
	
	private WorkRepository workRepository;
	
	private String instanceId = UUID.randomUUID().toString();
	private Optional<Integer> poolSize = Optional.empty();
	private Supplier<ConsumerRegister> consumerSupplier = () -> new ConsumerRegister();
	private TransactionalWorkerFactory transactionalWorkerFactory;
	
	private QueueConfig() {
	}

	public static QueueConfig withDefaults() {
		return new QueueConfig();
	}

    public QueueConfig setConsumerRegister(ConsumerRegister register) {
    	this.consumerSupplier = () -> register;
    	return this;
    }
    
    public QueueConfig setConsumerRegisterSupplier(Supplier<ConsumerRegister> consumerSupplier) {
		this.consumerSupplier = consumerSupplier;
		return this;
	}
    
	public QueueConfig setWorkRepository(WorkRepository workRepository) {
		this.workRepository = workRepository;
		return this;
	}
	
	public WorkRepository getWorkRepository() {
		return workRepository;
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
	
	public QueueConfig setTransactionalWorkerFactory(TransactionalWorkerFactory transactionalWorkerFactory) {
		this.transactionalWorkerFactory = transactionalWorkerFactory;
		return this;
	}

	public QueueConfig setInstanceId(String instanceId) {
		this.instanceId = instanceId;
		return this;
	}
	
	/**
	 * Initialize required components from config and return immutable context
	 */
	public QueueContext buildQueueContext() {
		final ConsumerRegister register = requireNonNull(consumerSupplier.get(), "Consumer register or supplier must be set in config");
		
		logger.info("Consumers configured {}", register);
		
		final WorkRepository repository = requireNonNull(workRepository, "Work Repoistory must be set in the config");
		final TransactionalWorkerFactory workerFactory = requireNonNull(transactionalWorkerFactory, "Transactional WorkerFactory must be set in config");
		
		return new QueueContext() {
			
			@Override
			public String getInstanceId() {
				return instanceId;
			}
			
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
