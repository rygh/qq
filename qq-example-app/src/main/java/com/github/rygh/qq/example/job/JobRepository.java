package com.github.rygh.qq.example.job;

import java.util.stream.Stream;

import org.springframework.data.repository.Repository;

public interface JobRepository extends Repository<Job, Long> {
	Stream<Job> findAll();
}
