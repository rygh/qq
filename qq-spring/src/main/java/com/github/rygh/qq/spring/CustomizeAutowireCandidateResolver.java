package com.github.rygh.qq.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.github.rygh.qq.WorkPublisher;

public class CustomizeAutowireCandidateResolver implements BeanFactoryPostProcessor {
	
	private final PublisherAutowireResolver autowireResolver;
	
	public CustomizeAutowireCandidateResolver(WorkPublisher publisher) {
		this.autowireResolver = new PublisherAutowireResolver(publisher);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof DefaultListableBeanFactory) {
			((DefaultListableBeanFactory) beanFactory).setAutowireCandidateResolver(autowireResolver);
		}
	}
}