package com.github.rygh.qq.example;


import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.EntityResolver;
import com.github.rygh.qq.QQServer;
import com.github.rygh.qq.QueueConfig;
import com.github.rygh.qq.WorkPublisher;
import com.github.rygh.qq.postgres.PostgresConsumerDefinitionRepository;
import com.github.rygh.qq.postgres.PostgresWorkRepository;
import com.github.rygh.qq.repositories.ConsumerDefinitionRepository;
import com.github.rygh.qq.repositories.WorkRepository;
import com.github.rygh.qq.spring.CustomizeAutowireCandidateResolver;
import com.github.rygh.qq.spring.QQSpringLifecycleBean;
import com.github.rygh.qq.spring.SpringConsumerRegisterSupplier;
import com.github.rygh.qq.spring.SpringTransactionalWorkerFactory;

@SpringBootApplication
public class QQExampleApplication {

	
	public TransactionTemplate createTransactionTemplate(DataSource ds, PlatformTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate;
	}
	
	@Bean
	public WorkRepository workRepository(DataSource ds, PlatformTransactionManager transactionManager) {
		return new PostgresWorkRepository(ds, createTransactionTemplate(ds, transactionManager));
	}
	
	@Bean
	public CustomizeAutowireCandidateResolver customizeAutowireCandidateResolver(WorkRepository workRepository, EntityResolver entityResolver) {
		return new CustomizeAutowireCandidateResolver(new WorkPublisher(workRepository, entityResolver));
	}
	
	@Bean
	public ConsumerDefinitionRepository consumerDefinitionRepository(DataSource ds) {
		return new PostgresConsumerDefinitionRepository(ds);
	}
	
	@Bean
	public QQSpringLifecycleBean queueLifecycle(
			WorkRepository workRepository, 
			DataSource ds, 
			PlatformTransactionManager transactionManager, 
			ApplicationContext context, 
			EntityResolver entityResolver, 
			ConsumerDefinitionRepository consumerDefinitionRepository) {
		
		QueueConfig config = QueueConfig.withDefaults()
			.setWorkRepository(workRepository)
			.setTransactionalWorkerFactory(new SpringTransactionalWorkerFactory(createTransactionTemplate(ds, transactionManager)))
			.setConsumerRegisterSupplier(new SpringConsumerRegisterSupplier(context, entityResolver))
			.setConsumerDefinitionRepository(consumerDefinitionRepository);
		
		return new QQSpringLifecycleBean(new QQServer(config));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(QQExampleApplication.class, args);
	}
}
