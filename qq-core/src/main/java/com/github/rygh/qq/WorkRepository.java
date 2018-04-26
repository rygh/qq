package com.github.rygh.qq;

import java.util.Optional;
import java.util.stream.Stream;

import com.github.rygh.qq.domain.Work;

public interface WorkRepository {
	Optional<Work> getWorkWithLock(Long id);
	Work store(Work work);
	Work update(Work work);
	Stream<Work> findFirst(int count);
	Stream<Work> claimNextReady(int count);
}
