package com.github.rygh.qq.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.rygh.qq.domain.ConsumerDefintition;
import com.github.rygh.qq.repositories.ConsumerDefinitionRepository;

public class PostgresConsumerDefinitionRepository implements ConsumerDefinitionRepository {

	private final NamedParameterJdbcTemplate jdbc;
	
	private final class ConsumerDefinitionRowMapper implements RowMapper<ConsumerDefintition> {
		@Override
		public ConsumerDefintition mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ConsumerDefintition(rs.getString("consumer_name"), rs.getString("description"), rs.getString("pool"), rs.getBoolean("enabled"));
		}
	}
	
	public PostgresConsumerDefinitionRepository(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setFetchSize(1000);
		jdbcTemplate.setLazyInit(false);
		
		this.jdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
	}
	
	@Override
	public Stream<ConsumerDefintition> findAll() {
		return jdbc.query("select * from consumer_definition", new ConsumerDefinitionRowMapper()).stream();
	}
}
