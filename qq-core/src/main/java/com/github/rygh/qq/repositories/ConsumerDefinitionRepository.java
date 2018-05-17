package com.github.rygh.qq.repositories;

import java.util.stream.Stream;

import com.github.rygh.qq.domain.ConsumerDefintition;

public interface ConsumerDefinitionRepository {
	Stream<ConsumerDefintition> findAll();
}
