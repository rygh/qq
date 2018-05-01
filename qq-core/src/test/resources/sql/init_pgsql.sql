CREATE TABLE work (
	id 					SERIAL PRIMARY KEY,
	created_time 		TIMESTAMP NOT NULL,
	started_time		TIMESTAMP,
	completed_time      TIMESTAMP,
	consumer 			VARCHAR(255) NOT NULL,
	entity_class 		VARCHAR(255) NOT NULL,
	entity_id 			VARCHAR(255) NOT NULL,
	state 				VARCHAR(20) NOT NULL,
	version             INT DEFAULT 1 NOT NULL
);

CREATE INDEX ix_work_state_ready ON work (state, created_time ASC) WHERE state = 'READY';
