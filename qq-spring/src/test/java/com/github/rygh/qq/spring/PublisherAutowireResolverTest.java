package com.github.rygh.qq.spring;

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.rygh.qq.QQContextHolder;
import com.github.rygh.qq.WorkPublisher;
import com.github.rygh.qq.annotations.QQPublish;
import com.github.rygh.qq.annotations.QQWorkerMethod;
import com.github.rygh.qq.domain.EntityId;
import com.github.rygh.qq.domain.Work;

@Configuration
public class PublisherAutowireResolverTest {

	private static List<String> captured = new ArrayList<>();
	
	@BeforeClass
	public static void beforeClass() {
		QQContextHolder.setContext(new NullQueueContext() {
			@Override
			public WorkPublisher getWorkPublisher() {
				return new WorkPublisher(null, null) {
					@Override
					public Work publish(Object payload, String consumer) {
						captured.add(consumer);
						return null;
					}
				};
			}
		});
	}
	
	public static interface SomeQueueInterface {
		@QQWorkerMethod
		default void doQueueStuff(Work work) {
			Assert.fail("Should call proxy, not implementation!");
		}
	}
	
	public static class SomeQueueClass {
		@QQWorkerMethod
		public void doQueueStuff(Work work) {
			Assert.fail("Should call proxy, not implementation!");
		}
	}
	
	public static class SomeBean {
		@QQPublish("SomeQueueInterface")
		private SomeQueueInterface queueInterface;
		
		@QQPublish("SomeQueueClass")
		private SomeQueueClass queueClass;
		
		public void doBeanStuff() {
			// Consumers should not accept work, but rather EntitySpec
			queueInterface.doQueueStuff(new Work(LocalDateTime.now(), new EntityId("1", Object.class), "SomeQueueInterface"));
			queueClass.doQueueStuff(new Work(LocalDateTime.now(), new EntityId("2", Object.class), "SomeQueueClass"));
		}
		
	}
	
	@Bean
	public static CustomizeBeanFactoryAutorireResolver setupBeanFactory() {
		return new CustomizeBeanFactoryAutorireResolver();
	}
	
	@Bean
	public SomeBean someBean() {
		return new SomeBean();
	}

	
	@Bean
	public SomeQueueInterface shouldNotBeUsed() {
		return new SomeQueueInterface() {
		};
	}
	
	@Bean
	public SomeQueueClass shouldNotBeUsedEither() {
		return new SomeQueueClass();
	}
	
	@Test
	public void shouldAutowireQueueProxy() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(getClass());
		
		SomeBean bean = context.getBean(SomeBean.class);
		bean.doBeanStuff();
	
		assertThat(captured, hasItems("SomeQueueInterface", "SomeQueueClass"));
		
		context.close();
	}
	
}
