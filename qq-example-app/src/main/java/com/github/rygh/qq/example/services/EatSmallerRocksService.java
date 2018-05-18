package com.github.rygh.qq.example.services;

import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQWorkerMethod;

@QQConsumer("eat-smaller-rocks")
public class EatSmallerRocksService {

	@QQWorkerMethod
	public void eatSmallRock(RockFragment frag) {
		
	}
	
}
