package com.github.rygh.qq.postgres;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.rygh.qq.repositories.WorkRepository;

public class PostgresWorkRepositoryTest extends AbstractWorkRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    
	private static WorkRepository repository;
    
    @BeforeClass
    public static void setup() {
    	
		PGSimpleDataSource pds = new PGSimpleDataSource();
		pds.setUser(postgres.getUsername());
		pds.setPassword(postgres.getPassword());
		pds.setURL(postgres.getJdbcUrl());

		try (Connection conn = pds.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/pgsql_schema.sql"));
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("sql/pgsql_testconfig.sql"));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

    	repository = new PostgresWorkRepository(pds);
    }

	@Override
	protected WorkRepository getInstance() {
		return repository;
	}
}
