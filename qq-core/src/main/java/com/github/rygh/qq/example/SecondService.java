package com.github.rygh.qq.example;

import java.util.function.Consumer;

import com.github.rygh.qq.domain.Work;

public class SecondService implements Consumer<Work> {

	@Override
	public void accept(Work t) {
		// Load Stuff from work
		// A dispatcher can be introduced where a procy implements the actual consumer
		// and calls the appropriate method based on annotation
		processStuff((Stuff) new Object());
	}
	
	
	public void processStuff(Stuff stuff) {
		System.out.println("Processing stuff " + stuff.getDescription());
	}
	
}
