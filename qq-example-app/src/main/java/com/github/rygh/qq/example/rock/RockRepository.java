package com.github.rygh.qq.example.rock;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface RockRepository extends CrudRepository<Rock, UUID> {
}
