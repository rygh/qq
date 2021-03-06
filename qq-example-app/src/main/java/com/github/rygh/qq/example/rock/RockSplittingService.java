package com.github.rygh.qq.example.rock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQPublish;
import com.github.rygh.qq.annotations.QQWorkerMethod;

@Service
@QQConsumer("rock-splitter")
public class RockSplittingService {

	private static final Logger logger = LoggerFactory.getLogger(RockSplittingService.class);

	private final EatSmallerRocksService eatSmallerRocksService;
	private final RockFragmentRepository repository;
	
	public RockSplittingService(@QQPublish("eat-smaller-rocks") EatSmallerRocksService eatSmallerRocksService, RockFragmentRepository repository) {
		this.eatSmallerRocksService = eatSmallerRocksService;
		this.repository = repository;
	}

	@QQWorkerMethod
	public void smash(Rock rock) {
		logger.info("Hey ho smashing {} good", rock);
		
		// Rockfragments must be persisted before we call the next service
		// No processing is started before this method returns and the transaction is comitted
		rock.smash()
			.stream()
			.map(repository::save)
			.forEach(eatSmallerRocksService::eatSmallRock);
	}
}
