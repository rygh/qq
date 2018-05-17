package com.github.rygh.qq.postgres;

import static java.util.Objects.requireNonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.rygh.qq.domain.Work;
import com.github.rygh.qq.domain.WorkState;
import com.github.rygh.qq.repositories.WorkRepository;

public class PostgresWorkRepository implements WorkRepository {

	private final class WorkRowMapper implements RowMapper<Work> {
		@Override
		public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Work(rs.getLong("id"), nullsafe(rs.getTimestamp("created_time")), rs.getString("entity_class"), rs.getString("entity_id"), rs.getString("consumer"))
				.setCompletedTime(nullsafe(rs.getTimestamp("completed_time")))
				.setStartedTime(nullsafe(rs.getTimestamp("started_time")))
				.setState(WorkState.valueOf(rs.getString("state")))
				.setVersion(rs.getInt("version"));
		}
		
		private LocalDateTime nullsafe(Timestamp ts) {
			return ts == null ? null : ts.toLocalDateTime();
		}
	}
	
	private final NamedParameterJdbcTemplate database;
	private TransactionTemplate transactionTemplate;
	
	public PostgresWorkRepository(DataSource dataSource, TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setFetchSize(1000);
		jdbcTemplate.setLazyInit(false);
		
		this.database = new NamedParameterJdbcTemplate(jdbcTemplate);
	}
	
	@Override
	public Stream<Work> findFirst(int count) {
		String sql = "select * from work order by created_time asc limit :limit";
		return database.query(sql, new MapSqlParameterSource("limit", count), new WorkRowMapper()).stream();
	}

	/**
	 * Returns the work entity if not locked by any other thread
	 * Entity will remain locked for the period of the external transaction this method in called in
	 */
	@Override
	public Optional<Work> getByIdWithLock(Long id) {
		String sql = "select * from work where id = :id for update skip locked";
		return database.query(sql, new MapSqlParameterSource("id", id), new WorkRowMapper()).stream().findFirst();
	}
	
	@Override
	public Optional<Work> getById(Long id) {
		String sql = "select * from work where id = :id";
		return database.query(sql, new MapSqlParameterSource("id", id), new WorkRowMapper()).stream().findFirst();
	}

	@Override
	public Work store(Work work) {
		String sql = 
				"insert into work ("
				+ " created_time, "
				+ " consumer, "
				+ " entity_class, "
				+ " entity_id, "
				+ " state, "
				+ " version "
				+ ") values ("
				+ " :created, "
				+ " :consumer, "
				+ " :entityClass, "
				+ " :entityId, "
				+ " :state, "
				+ " :version "
				+ ") returning id";
		
		SqlParameterSource params = new MapSqlParameterSource("created", work.getCreatedTime())
			.addValue("consumer", work.getConsumer())
			.addValue("entityClass", work.getEntityType())
			.addValue("entityId", work.getEntityId())
			.addValue("state", work.getState().name())
			.addValue("version",  work.getVersion());
		
		Long assignedSerialId = database.queryForObject(sql, params, Long.class);
		return work.setId(requireNonNull(assignedSerialId, "Assigned id was null when storing " + work + ", it's a horrible situation!"));
	}

	/**
	 * Updates started/completed time and state if set.
	 * Will check version against stored version to prevent update on a dirty record.
	 */
	@Override
	public Work update(Work work) {
		String sql = "update work set"
			+ " started_time = :started, "
			+ " completed_time = :completed, "
			+ " state = :state, "
			+ " version = :nextVersion "
			+ " where id = :id "
			+ " and version = :currentVersion";
		
		int nextVersion = work.nextVersion();
		SqlParameterSource params = new MapSqlParameterSource("id", work.getId())
			.addValue("started", work.getStartedTime())
			.addValue("completed", work.getCompletedTime())
			.addValue("state", work.getState().name())
			.addValue("nextVersion", nextVersion)
			.addValue("currentVersion", work.getVersion());
		
		if (database.update(sql, params) != 1) {
			throw new OptimisticLockingFailureException("Failed to update " + work + ", version has changed!");
		}
		
		return work.setVersion(nextVersion);
	}
 
	/**
	 * This method has its own transaction and does three actions
	 * - Items are locked up to the max count, skip locked is used so 
	 * - Locked items are updated with the PROCESSING state to prevent visibility to others after the transaction
	 * - The updated items are returned to the caller
	 */
	@Override
	public Stream<Work> claimNextReadyForPool(int count, String pool) {
		String sql = 
			"update work "
			+ "set state = :processing "
			+ "where id in "
			+ "("
			+ "	select w.id "
			+ " from work as w, consumer_definition as cd"
			+ " where w.state = :state "
			+ "   and cd.consumer_name = w.consumer "
			+ "   and cd.pool = :pool "
			+ "   and cd.enabled = TRUE "
			+ " for update skip locked "
			+ " limit :limit"
			+ ") "
			+ "returning *";
		
		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("processing", WorkState.PROCESSING.name())
			.addValue("state", WorkState.READY.name())
			.addValue("pool", pool)
			.addValue("limit", count);
		
		return transactionTemplate.execute(tx -> database.query(sql, params, new WorkRowMapper()).stream());
	}

}
