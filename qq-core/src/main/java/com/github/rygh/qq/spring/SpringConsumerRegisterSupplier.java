package com.github.rygh.qq.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;

import com.github.rygh.qq.ConsumerRegister;
import com.github.rygh.qq.annotations.QQConsumer;
import com.github.rygh.qq.annotations.QQWorkerMethod;
import com.github.rygh.qq.domain.Work;

public class SpringConsumerRegisterSupplier {

	private ApplicationContext applicationContext;
	
	public SpringConsumerRegisterSupplier(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ConsumerRegister createConsumerRegister() {

		ConsumerRegister register = new ConsumerRegister();
		
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(QQConsumer.class);
		for (Object bean : beans.values()) {
			QQConsumer specification = bean.getClass().getAnnotation(QQConsumer.class);
			register.register(specification.value(), new ReflectiveConsumer(bean));
		}
		return register;
	}
	
	class ReflectiveConsumer implements Consumer<Work> {
		private final Object bean;
		private final Method target;
		
		public ReflectiveConsumer(Object bean) {
			this.bean = bean;
			this.target = Stream.of(bean.getClass().getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(QQWorkerMethod.class))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Annotated bean "+bean.getClass()+" does not contain any WorkerMethods"));
		}

		@Override
		public void accept(Work work) {
			try {
				target.invoke(bean, work);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String toString() {
			return "ReflectiveConsumer [bean=" + bean.getClass() + ", target=" + target + "]";
		}
	}
}
