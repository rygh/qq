package com.github.rygh.qq.example.rock;

import org.springframework.stereotype.Service;

import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQWorkerMethod;

@Service
@QQConsumer("eat-smaller-rocks")
public class EatSmallerRocksService {

	@QQWorkerMethod
	public void eatSmallRock(RockFragment frag) {
		
	}
	
}
