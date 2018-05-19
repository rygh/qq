package com.github.rygh.qq.spring;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;

import com.github.rygh.qq.QQContextHolder;
import com.github.rygh.qq.annotations.QQPublish;
import com.github.rygh.qq.annotations.QQWorkerMethod;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

class PublisherAutowireResolver extends SimpleAutowireCandidateResolver {

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
					
					// Yeah so this is to get lazy behaviour and prevent nasty cycles of dependencies.
					// I _could_ (and maybe I should) do this in a later BeanPostProcessor instead - but then again that means different nastyness.
					QQContextHolder.getContext().getWorkPublisher().publish(args[0], specification.value());
					return null;
				}
			};
			
			try {
				return new ByteBuddy()
				  .subclass(descriptor.getDependencyType())
				  .method(isDeclaredBy(descriptor.getDependencyType())
						  .and(isPublic())
						  .and(isAnnotatedWith(QQWorkerMethod.class)))
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
