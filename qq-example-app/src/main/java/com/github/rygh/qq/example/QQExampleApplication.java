package com.github.rygh.qq.example;


import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.rygh.qq.EntityResolver;
import com.github.rygh.qq.QQConfig;
import com.github.rygh.qq.QQServer;
import com.github.rygh.qq.jpa.JpaEntityResolver;
import com.github.rygh.qq.postgres.PostgresConsumerDefinitionRepository;
import com.github.rygh.qq.postgres.PostgresWorkRepository;
import com.github.rygh.qq.spring.CustomizeBeanFactoryAutowireResolver;
import com.github.rygh.qq.spring.QQSpringLifecycleBean;
import com.github.rygh.qq.spring.SpringConsumerRegisterSupplier;
import com.github.rygh.qq.spring.SpringTransactionWrapper;

@SpringBootApplication
public class QQExampleApplication {
	
	@Bean
	public static CustomizeBeanFactoryAutowireResolver customizeAutowireCandidateResolver() {
		return new CustomizeBeanFactoryAutowireResolver();
	}

	@Bean
	public QQSpringLifecycleBean queueLifecycle(
			DataSource ds,  PlatformTransactionManager transactionManager, 
			ApplicationContext applicationContext, EntityManager entityManager) {
		
		EntityResolver entityResolver = new JpaEntityResolver(entityManager);
		
		QQConfig config = QQConfig.withDefaults()
			.setWorkRepository(new PostgresWorkRepository(ds))
			.setTransactionWrapper(new SpringTransactionWrapper(transactionManager))
			.setConsumerRegisterSupplier(new SpringConsumerRegisterSupplier(applicationContext, entityResolver))
			.setConsumerDefinitionRepository(new PostgresConsumerDefinitionRepository(ds))
			.setEntityResolver(entityResolver);
		
		return new QQSpringLifecycleBean(new QQServer(config));
	}
	
	public static void main(String[] args) {
		new SpringApplication(QQExampleApplication.class).run(args);
	}
}
