package com.github.rygh.qq;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.domain.ConsumerDefintition;
import com.github.rygh.qq.domain.ConsumerRegister;
import com.github.rygh.qq.domain.PoolDefinition;
import com.github.rygh.qq.domain.QueueDefinitions;
import com.github.rygh.qq.domain.QueueDefinitions.DefaultPoolDefinition;
import com.github.rygh.qq.repositories.ConsumerDefinitionRepository;
import com.github.rygh.qq.repositories.WorkRepository;

public class QueueConfig {

	private static final Logger logger = LoggerFactory.getLogger(QueueConfig.class);
	
	private WorkRepository workRepository;
	private ConsumerDefinitionRepository consumerDefinitionRepository;
	
	private String instanceId = UUID.randomUUID().toString();
	private Optional<Integer> defaultCorePoolSize = Optional.empty();
	private Optional<Integer> defaultMaxPoolSize = Optional.empty();
	
	private Supplier<ConsumerRegister> consumerSupplier = () -> new ConsumerRegister();
	private TransactionalWorkerFactory transactionalWorkerFactory;
	private Set<PoolDefinition> poolDefinitions = new HashSet<>();
	private EntityResolver entityResolver;
	
	private QueueConfig() {
	}

	public static QueueConfig withDefaults() {
		return new QueueConfig();
	}

    public QueueConfig setConsumerRegister(ConsumerRegister register) {
    	this.consumerSupplier = () -> register;
    	return this;
    }
    
    public QueueConfig setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
		return this;
	}
    
    public QueueConfig setConsumerRegisterSupplier(Supplier<ConsumerRegister> supplier) {
    	this.consumerSupplier = supplier;
    	return this;
    }
    
	public QueueConfig setWorkRepository(WorkRepository workRepository) {
		this.workRepository = workRepository;
		return this;
	}
	
	public QueueConfig setConsumerDefinitionRepository(ConsumerDefinitionRepository consumerDefinitionRepository) {
		this.consumerDefinitionRepository = consumerDefinitionRepository;
		return this;
	}
	
	public WorkRepository getWorkRepository() {
		return workRepository;
	}
	
	public QueueConfig setDefaultCorePoolSize(int size) {
		defaultCorePoolSize = Optional.of(size);
		return this;
	}
	
	public QueueConfig setPoolDefinitions(Set<PoolDefinition> definitions) {
		this.poolDefinitions = new HashSet<>(definitions);
		return this;
	}
	
	private int getDefaultCorePoolSize() {
		return defaultCorePoolSize.orElseGet(Runtime.getRuntime()::availableProcessors);
	}
	
	private int getDefaultMaxPoolSize() {
		return defaultMaxPoolSize.orElseGet(() -> getDefaultCorePoolSize() * 5);
	}
	
	public QueueConfig setDefaultMaxPoolSize(int size) {
		defaultMaxPoolSize = Optional.of(size);
		return this;
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
		
		List<ConsumerDefintition> definitions = consumerDefinitionRepository.findAll().collect(Collectors.toList());
		DefaultPoolDefinition defaultPoolBuilder = new QueueDefinitions.DefaultPoolDefinition(getDefaultCorePoolSize(), getDefaultMaxPoolSize());
		final QueueDefinitions queueDefinitions = new QueueDefinitions(definitions, poolDefinitions, defaultPoolBuilder);
		
		WorkPublisher publisher = new WorkPublisher(workRepository, entityResolver);
		register.verifyConsumers(queueDefinitions);
		
		QueueContext context = new QueueContext() {
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

			@Override
			public Set<PoolDefinition> getWorkerPools() {
				return queueDefinitions.getPoolDefinitions();
			}

			@Override
			public WorkPublisher getWorkPublisher() {
				return publisher;
			}
		};
		
		
		return context;
	}
	
    @Override
	public String toString() {
		return "Current QueueConfig\n"
				+ "* WorkRepository......" + getWorkRepository().getClass() + "\n"
				+ "* MaxPoolSize........." + getDefaultMaxPoolSize() + "\n"
				+ "* CorePoolSize........" + getDefaultCorePoolSize() + "\n"
				+ "* PollingFrequency...." + getPollingFrequency() + "\n";
	}
}
