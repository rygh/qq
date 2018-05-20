package com.github.rygh.qq.example.rock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQWorkerMethod;

@Service
@QQConsumer("eat-smaller-rocks")
public class EatSmallerRocksService {

	private static final Logger logger = LoggerFactory.getLogger(EatSmallerRocksService.class);
	
	@QQWorkerMethod
	public void eatSmallRock(RockFragment frag) {
		logger.info("Yummy yummy yummy I got rocks in my tummy, Done eating {}", frag);
	}
	
}
