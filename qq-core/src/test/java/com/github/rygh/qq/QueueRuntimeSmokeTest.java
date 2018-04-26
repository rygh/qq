package com.github.rygh.qq;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.github.rygh.qq.pgsql.PostgresWorkRepository;

public class QueueRuntimeSmokeTest {

	public static void main(String ...strings ) {
	
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUser("postgres");
		ds.setPassword("secret");
		ds.setURL("jdbc:postgresql://localhost:5432/qqtest");
		
		QueueConfig config = QueueConfig.withDefaults();
		config.setPlatformTransactionManager(new DataSourceTransactionManager(ds));
		WorkRepository workRepository = new PostgresWorkRepository(ds, config.createTransactionTemplate());
		
		WorkPublisher publisher = new WorkPublisher(workRepository);
		publisher.publish(Object.class, UUID.randomUUID().toString(), "TestQueue");
		publisher.publish(Object.class, UUID.randomUUID().toString(), "TestQueue");
		
		config.setWorkRepository(workRepository);
		
		QQServer runtime = new QQServer(config);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(6000);
					runtime.stop();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}, "assassin").start();		
		
		runtime.start();
	}
	
	
}
