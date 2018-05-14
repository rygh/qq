package com.github.rygh.qq;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;
import com.github.rygh.qq.spring.SpringConfigurationFactory;

public class PostgresContainerSmokeTest {

	private final static Logger logger = LoggerFactory.getLogger(PostgresContainerSmokeTest.class);
	
    @NotNull
    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");
    
    private DataSource dataSource;
    
    private DataSource getOrCreateDataSource() {
    	if (dataSource == null) {
    		PGSimpleDataSource pds = new PGSimpleDataSource();
    		pds.setUser(postgres.getUsername());
    		pds.setPassword(postgres.getPassword());
    		pds.setURL(postgres.getJdbcUrl());

    		dataSource = pds;
    		
    		try (Connection conn = dataSource.getConnection()) {
    			ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/init_pgsql.sql"));
    		} catch (SQLException e) {
    			throw new RuntimeException(e);
    		}
    	}
    	
    	return dataSource;
    }
    
    @Test(timeout = 15_000)
    public void publishWorkAndStartConsumer() throws SQLException {
    	
    	QueueConfig config = SpringConfigurationFactory.withSpringDefaults(getOrCreateDataSource());
    	WorkRepository workRepository = config.getWorkRepository();
    	
    	WorkEntityResolver workEntityResolver = new WorkEntityResolver() {
			@Override
			public EntityId extractEntityId(Object obj) {
				return new EntityId(obj.toString(), obj.getClass());
			}

			@Override
			public <T> T loadEntity(EntityId id) {
				return null;
			}
		};
    	
    	WorkPublisher publisher = new WorkPublisher(workRepository, workEntityResolver);
    	TestQueue testQueueConsumer = new TestQueue(publisher);
    	SecondQueue secondQueueConsumer = new SecondQueue();
    	
    	ConsumerRegister register = new ConsumerRegister()
    		.register("TestQueue", testQueueConsumer::doSomeWork)
    		.register("SecondQueue", secondQueueConsumer::doSomeMoreWork);
    	
    	config.setConsumerRegister(register);
    	
		Work w1 = publisher.publish(UUID.randomUUID().toString(), "TestQueue");
		Work w2 = publisher.publish(UUID.randomUUID().toString(), "TestQueue");
		
		QQServer runtime = new QQServer(config);
		runtime.start();

		while (workRepository.findFirst(10).count() > 0) {
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		
		assertTrue(workRepository.getById(w1.getId()).get().is(WorkState.COMPLETED));
		assertTrue(workRepository.getById(w2.getId()).get().is(WorkState.COMPLETED));
		
		runtime.stop();
    }


    class TestQueue {
    	
    	private WorkPublisher publisher;
    	
    	public TestQueue(WorkPublisher publisher) {
    		this.publisher = publisher;
    	}
    	
    	public void doSomeWork(EntityId work) {
    		logger.info("TestQueue, processing {}", work);
    		
    		Work published = publisher.publish("123", "SecondQueue");
    		logger.info("Requested extra work {}", published);
    	}
    }
    
    class SecondQueue {
    	public void doSomeMoreWork(EntityId work) {
    		logger.info("SecondQueue, processing {}", work);
    	}
    }
}
