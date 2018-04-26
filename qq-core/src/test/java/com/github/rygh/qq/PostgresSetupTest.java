package com.github.rygh.qq;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

public class PostgresSetupTest {

	@Test
	public void kake() throws SQLException {
		
		PGSimpleDataSource pgds = new PGSimpleDataSource();
		pgds.setUser("postgres");
		pgds.setPassword("secret");
		pgds.setURL("jdbc:postgresql://localhost:5432/qqtest");
	
		
		try (Connection connection = pgds.getConnection()) {
			System.err.println(connection.isValid(1000));
		}
	}
	
}
