package com.github.rygh.qq.example;

import com.github.rygh.qq.WorkPublisher;
import com.github.rygh.qq.domain.Work;

public class FirstService {

	private WorkPublisher publisher = new WorkPublisher();
	
	public void doStuffAndCallNext() {
		
		System.out.println("Created some stuff!");
		
		Stuff stuff = new Stuff(10L, "First stuffs, with id = 10");
		
		publisher.publish(Work.processEntity(stuff.getId(), stuff.getClass()).withService(SecondService.class));
	}
	
	
}
