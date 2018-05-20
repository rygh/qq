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

public class QQConfig {

	private static final Logger logger = LoggerFactory.getLogger(QQConfig.class);
	
	private WorkRepository workRepository;
	private ConsumerDefinitionRepository consumerDefinitionRepository;
	
	private String instanceId = UUID.randomUUID().toString();
	private Optional<Integer> defaultCorePoolSize = Optional.empty();
	private Optional<Integer> defaultMaxPoolSize = Optional.empty();
	
	private Supplier<ConsumerRegister> consumerSupplier = () -> new ConsumerRegister();
	private TransactionalWorkerFactory transactionalWorkerFactory;
	private Set<PoolDefinition> poolDefinitions = new HashSet<>();
	private EntityResolver entityResolver;
	
	private QQConfig() {
	}

	public static QQConfig withDefaults() {
		return new QQConfig();
	}

    public QQConfig setConsumerRegister(ConsumerRegister register) {
    	this.consumerSupplier = () -> register;
    	return this;
    }
    
    public QQConfig setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
		return this;
	}
    
    public QQConfig setConsumerRegisterSupplier(Supplier<ConsumerRegister> supplier) {
    	this.consumerSupplier = supplier;
    	return this;
    }
    
	public QQConfig setWorkRepository(WorkRepository workRepository) {
		this.workRepository = workRepository;
		return this;
	}
	
	public QQConfig setConsumerDefinitionRepository(ConsumerDefinitionRepository consumerDefinitionRepository) {
		this.consumerDefinitionRepository = consumerDefinitionRepository;
		return this;
	}
	
	public WorkRepository getWorkRepository() {
		return workRepository;
	}
	
	public QQConfig setDefaultCorePoolSize(int size) {
		defaultCorePoolSize = Optional.of(size);
		return this;
	}
	
	public QQConfig setPoolDefinitions(Set<PoolDefinition> definitions) {
		this.poolDefinitions = new HashSet<>(definitions);
		return this;
	}
	
	private int getDefaultCorePoolSize() {
		return defaultCorePoolSize.orElseGet(Runtime.getRuntime()::availableProcessors);
	}
	
	private int getDefaultMaxPoolSize() {
		return defaultMaxPoolSize.orElseGet(() -> getDefaultCorePoolSize() * 5);
	}
	
	public QQConfig setDefaultMaxPoolSize(int size) {
		defaultMaxPoolSize = Optional.of(size);
		return this;
	}
	
	public Duration getPollingFrequency() {
		return Duration.of(1L, ChronoUnit.SECONDS);
	}
	
	public QQConfig setTransactionalWorkerFactory(TransactionalWorkerFactory transactionalWorkerFactory) {
		this.transactionalWorkerFactory = transactionalWorkerFactory;
		return this;
	}

	public QQConfig setInstanceId(String instanceId) {
		this.instanceId = instanceId;
		return this;
	}
	
	/**
	 * Initialize required components from config and return immutable context
	 */
	public QQContext buildQueueContext() {
		final ConsumerRegister register = requireNonNull(consumerSupplier.get(), "Consumer register or supplier must be set in config");
		
		logger.info("Consumers configured {}", register);
		
		final WorkRepository repository = requireNonNull(workRepository, "Work Repoistory must be set in the config");
		final TransactionalWorkerFactory workerFactory = requireNonNull(transactionalWorkerFactory, "Transactional WorkerFactory must be set in config");
		
		List<ConsumerDefintition> definitions = consumerDefinitionRepository.findAll().collect(Collectors.toList());
		DefaultPoolDefinition defaultPoolBuilder = new QueueDefinitions.DefaultPoolDefinition(getDefaultCorePoolSize(), getDefaultMaxPoolSize());
		final QueueDefinitions queueDefinitions = new QueueDefinitions(definitions, poolDefinitions, defaultPoolBuilder);
		
		WorkPublisher publisher = new WorkPublisher(workRepository, entityResolver);
		register.verifyConsumers(queueDefinitions);
		
		QQContext context = new QQContext() {
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
		
		QQContextHolder.setContext(context);
		
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