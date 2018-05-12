package com.github.rygh.qq.spring;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.QueueConfig;

public class SpringConfigurationFactory {

	public static QueueConfig withSpringDefaults(DataSource ds) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		
		QueueConfig config = QueueConfig.withDefaults();
		config.setTransactionalWorkerFactory(new SpringTransactionalWorkerFactory(transactionTemplate));
		
		// TODO: Remove transaction management from repository
		config.setWorkRepository(new PostgresWorkRepository(ds, transactionTemplate));
		
		return config;
	}
}
