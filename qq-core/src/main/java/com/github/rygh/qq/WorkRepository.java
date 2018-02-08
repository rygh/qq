package com.github.rygh.qq;

import java.util.List;

import com.github.rygh.qq.domain.Work;

public interface WorkRepository {
	List<Work> findWork();
	Work getWork(Long id);
	Work store(Work work);
	Work update(Work work);
}
