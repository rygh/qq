CREATE TABLE consumer_definition (
	consumer_name       VARCHAR(255) PRIMARY KEY,
	description         VARCHAR(1024) NOT NULL,
	enabled             BOOLEAN DEFAULT TRUE NOT NULL,
	pool                VARCHAR(50) NOT NULL
);

CREATE TABLE work (
	id 					SERIAL PRIMARY KEY,
	created_time 		TIMESTAMP NOT NULL,
	started_time		TIMESTAMP,
	completed_time      TIMESTAMP,
	entity_class 		VARCHAR(255) NOT NULL,
	entity_id 			VARCHAR(255) NOT NULL,
	entity_id_class     VARCHAR(255) NOT NULL,
	state 				VARCHAR(20) NOT NULL,
	version             INT DEFAULT 1 NOT NULL,
	consumer 			VARCHAR(255) REFERENCES consumer_definition (consumer_name),
	error_message       VARCHAR(1024),
	execution_count     INT DEFAULT 0 NOT NULL
);

CREATE INDEX ix_work_state_ready ON work (state, created_time ASC) WHERE state = 'READY';
