package com.github.rygh.qq.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQPublish;
import com.github.rygh.qq.annotations.QQWorkerMethod;

@QQConsumer("rock-splitter")
public class RockSplittingService {

	private static final Logger logger = LoggerFactory.getLogger(RockSplittingService.class);

	@QQPublish("rock-eater")
	private EatSmallerRocksService eatSmallerRocksService;
	
	@QQWorkerMethod
	public void smash(Rock rock) {
		logger.info("Hey ho smashing {} good", rock);
		rock.smash().forEach(fragged -> {
			// Need to make it persistent.. could perhaps extend entity-service to persist before dispatch also hmm
			// Each fragment is processed asynchronously on the thread pool
			eatSmallerRocksService.eatSmallRock(fragged);
		});
	}
}
