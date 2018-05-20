package com.github.rygh.qq.spring;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.QQConfig;
import com.github.rygh.qq.postgres.PostgresWorkRepository;

@Deprecated
public class SpringConfigurationFactory {

	public static QQConfig withSpringDefaults(DataSource ds) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(ds));
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		
		return QQConfig.withDefaults()
			.setTransactionalWorkerFactory(new SpringTransactionalWorkerFactory(transactionTemplate))
			.setWorkRepository(new PostgresWorkRepository(ds, transactionTemplate));
	}
}
