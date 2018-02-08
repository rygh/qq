package com.github.rygh.qq;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import com.github.rygh.qq.domain.Work;

public class QueueConfig {

	
	private QueueConfig() {
	}

	public static QueueConfig withDefaults() {
		return new QueueConfig();
	}
	
	public WorkRepository getWorkRepository() {
		return new WorkRepository() {
			
			@Override
			public Work update(Work work) {
				return null;
			}
			
			@Override
			public Work store(Work work) {
				return null;
			}
			
			@Override
			public Work getWork(Long id) {
				return null;
			}
			
			@Override
			public List<Work> findWork() {
				return Arrays.asList(new Work(), new Work());
			}
		};
	}
	
	public int getCorePoolSize() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	public int getMaxPoolSize() {
		return getCorePoolSize() * 5;
	}
	
	public Duration getPollingFrequency() {
		return Duration.of(1L, ChronoUnit.SECONDS);
	}


	@Override
	public String toString() {
		return "Current QueueConfig\n"
				+ "* WorkRepository......" + getWorkRepository().getClass() + "\n"
				+ "* MaxPoolSize........." + getMaxPoolSize() + "\n"
				+ "* CorePoolSize........" + getCorePoolSize() + "\n"
				+ "* PollingFrequency...." + getPollingFrequency() + "\n";
		
	}
}
