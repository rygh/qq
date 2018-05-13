package com.github.rygh.qq.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;

import com.github.rygh.qq.WorkPublisher;
import com.github.rygh.qq.annotations.QQPublish;
import com.github.rygh.qq.annotations.QQWorkerMethod;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class PublisherAutowireResolver extends SimpleAutowireCandidateResolver {

	private final WorkPublisher publisher;

	public PublisherAutowireResolver(WorkPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public boolean isRequired(DependencyDescriptor descriptor) {
		QQPublish specification = descriptor.getAnnotation(QQPublish.class);
		return specification != null ? false : super.isRequired(descriptor);
	}

	@Override
	public Object getSuggestedValue(DependencyDescriptor descriptor) {
		QQPublish specification = descriptor.getAnnotation(QQPublish.class);
		if (specification != null) {
			InvocationHandler handler = new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if (args.length != 1) {
						throw new IllegalArgumentException("WorkerMethod can only take a single parameter");
					}
					
					publisher.publish(args[0], specification.value());
					return null;
				}
			};
			
			try {
				return new ByteBuddy()
				  .subclass(descriptor.getDependencyType())
				  .method(
						  ElementMatchers.isDeclaredBy(descriptor.getDependencyType())
						  .and(ElementMatchers.isPublic())
						  .and(ElementMatchers.isAnnotatedWith(QQWorkerMethod.class)))
				  .intercept(InvocationHandlerAdapter.of(handler))
				  .make()
				  .load(descriptor.getDeclaredType().getClassLoader())
				  .getLoaded()
				  .newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
		}
		
		return super.getSuggestedValue(descriptor);
	}
}
