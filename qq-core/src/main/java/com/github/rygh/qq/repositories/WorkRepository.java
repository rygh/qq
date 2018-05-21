package com.github.rygh.qq.repositories;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.rygh.qq.domain.Work;

public interface WorkRepository {
	Optional<Work> getByIdWithLock(Long id);
	Optional<Work> getById(Long id);
	Work store(Work work);
	Work update(Work work);
	Stream<Work> claimNextReadyForPool(int count, String pool);
}
