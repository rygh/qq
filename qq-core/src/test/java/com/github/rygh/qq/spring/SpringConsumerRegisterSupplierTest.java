package com.github.rygh.qq.spring;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.rygh.qq.WorkEntityResolver;
import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQWorkerMethod;
import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;

@Configuration
public class SpringConsumerRegisterSupplierTest {

	@Test
	public void shouldRegisterAnnotatedBeans() {

		WorkEntityResolver entityResolver = new WorkEntityResolver() {
			@Override
			public <T> T loadEntity(EntityId id) {
				return null;
			}
			
			@Override
			public EntityId extractEntityId(Object obj) {
				return null;
			}
		};
		
		AnnotationConfigApplicationContext applicationContext = 
			new AnnotationConfigApplicationContext(SpringConsumerRegisterSupplierTest.class);
		
		SpringConsumerRegisterSupplier supplier = 
			new SpringConsumerRegisterSupplier(applicationContext, entityResolver);
		Set<String> registeredConsumers =  supplier.createConsumerRegister().getRegisteredConsumers();
		
		assertEquals(2, registeredConsumers.size());
		assertThat(registeredConsumers, hasItems("WhateverQueue", "SomeOtherQueue"));
	}
	
	@QQConsumer("WhateverQueue")
	public class WhateverConsumer {
		@QQWorkerMethod
		public void doSomeWork(Work work) {
		}
	}
	
	@QQConsumer("SomeOtherQueue")
	public class SomeOtherQueue {
		@QQWorkerMethod
		public void doSomeOtherWork(Work work) {
		}
	}
	
	public class RandomBean {
		public void iCanWorkToo(Work work) {
		}
	}
	
	@Bean
	public WhateverConsumer whateverConsumerBean() {
		return new WhateverConsumer();
	}
	
	@Bean
	public SomeOtherQueue someOtherQueueBean() {
		return new SomeOtherQueue();
	}
	
	@Bean
	public RandomBean randomBean() {
		return new RandomBean();
	}
}

