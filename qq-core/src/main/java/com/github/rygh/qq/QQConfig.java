package com.github.rygh.qq;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.github.rygh.qq.error.DefaultErrorHandler;
import com.github.rygh.qq.error.ErrorHandler;
import com.github.rygh.qq.repositories.ConsumerDefinitionRepository;
import com.github.rygh.qq.repositories.WorkRepository;

public class QQConfig {

	private static final Logger logger = LoggerFactory.getLogger(QQConfig.class);
	
	private WorkRepository workRepository;
	private ConsumerDefinitionRepository consumerDefinitionRepository;
	
	private String instanceId = UUID.randomUUID().toString();
	private Optional<Integer> defaultCorePoolSize = Optional.empty();
	private Optional<Integer> defaultMaxPoolSize = Optional.empty();
	
	private Supplier<ConsumerRegister> consumerSupplier;
	private TransactionWrapper transactionWrapper;
	private Set<PoolDefinition> poolDefinitions = new HashSet<>();
	private EntityResolver entityResolver;
	
	private ErrorHandler errorHandler = new DefaultErrorHandler();
	
	private QQConfig() {
	}

	public static QQConfig withDefaults() {
		return new QQConfig();
	}

	private TransactionWrapper getTransactionWrapper() {
		return requireNonNull(transactionWrapper, "TransactionWrapper must be set in config");
	}
	
    public QQConfig setConsumerRegister(ConsumerRegister register) {
    	this.consumerSupplier = () -> register;
    	return this;
    }
    
    public QQConfig setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
		return this;
	}
    
    private EntityResolver getEntityResolver() {
		return requireNonNull(entityResolver, "EntityResolver must be set in config");
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
	
	public QQConfig setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}
	
	private ConsumerDefinitionRepository getConsumerDefinitionRepository() {
		return requireNonNull(consumerDefinitionRepository, "ConsumerDefinitionRepository must be set in config");
	}
	
	public QQConfig setTransactionWrapper(TransactionWrapper wrapper) {
		this.transactionWrapper = wrapper;
		return this;
	}
	
	private WorkRepository getWorkRepository() {
		return requireNonNull(workRepository, "WorkRepository must be set in config");
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
	
	public QQConfig setInstanceId(String instanceId) {
		this.instanceId = instanceId;
		return this;
	}
	
	private Supplier<ConsumerRegister> getConsumerRegisterSupplier() {
		return requireNonNull(consumerSupplier, "Consumer register or supplier must be set in config"); 
	}
	
	/**
	 * Initialize required components from config and return immutable context
	 */
	public QQContext buildQueueContext() {
		final ConsumerRegister register = getConsumerRegisterSupplier().get();
		logger.info("Consumers configured {}", register);
		
		final WorkRepository repository = getWorkRepository();
		final TransactionWrapper wrapper = getTransactionWrapper();
		final ErrorHandler error = errorHandler;
		final String id = instanceId;
		
		List<ConsumerDefintition> definitions = getConsumerDefinitionRepository().findAll().collect(Collectors.toList());
		DefaultPoolDefinition defaultPoolBuilder = new QueueDefinitions.DefaultPoolDefinition(getDefaultCorePoolSize(), getDefaultMaxPoolSize());
		final QueueDefinitions queueDefinitions = new QueueDefinitions(definitions, poolDefinitions, defaultPoolBuilder);
		
		WorkPublisher publisher = new WorkPublisher(repository, getEntityResolver());
		register.verifyConsumers(queueDefinitions);
		
		QQContext context = new QQContext() {
			@Override
			public String getInstanceId() {
				return id;
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
			public Set<PoolDefinition> getWorkerPools() {
				return queueDefinitions.getPoolDefinitions();
			}
			
			@Override
			public Map<String, ConsumerDefintition> getConsumerDefinitions() {
				return queueDefinitions.getConsumerDefinitions();
			}

			@Override
			public WorkPublisher getWorkPublisher() {
				return publisher;
			}

			@Override
			public TransactionWrapper getTransactionWrapper() {
				return wrapper;
			}

			@Override
			public ErrorHandler getErrorHandler() {
				return error;
			}
		};
		
		QQContextHolder.setContext(context);
		
		return context;
	}
	
    @Override
	public String toString() {
		return "Current QueueConfig\n"
			+ "* InstanceId.........." + instanceId + "\n"
			+ "* MaxPoolSize........." + getDefaultMaxPoolSize() + "\n"
			+ "* CorePoolSize........" + getDefaultCorePoolSize() + "\n"
			+ "* PollingFrequency...." + getPollingFrequency() + "\n"
			+ "* WorkRepository......" + workRepository + "\n"
			+ "* ConsumerRepository.." + consumerDefinitionRepository + "\n"
			+ "* EntityResolver......" + entityResolver + "\n"
			+ "* TransactionWrapper.." + transactionWrapper + "\n"
			+ "* ConsumerRegister...." + consumerSupplier + "\n"
			+ "* ErrorHandler........" + errorHandler + "\n"
		;
	}
}
