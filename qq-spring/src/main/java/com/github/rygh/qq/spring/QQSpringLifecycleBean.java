package com.github.rygh.qq.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import com.github.rygh.qq.QQServer;

public class QQSpringLifecycleBean implements SmartLifecycle, ApplicationContextAware {

	private QQServer runtime;
	private ApplicationContext applicationContext;
	private boolean running = false;
	
	@Override
	public void start() {
		QQServer runtime = applicationContext.getBean(QQServer.class);
		runtime.start();
		running = true;
	}

	@Override
	public void stop() {
		runtime.stop();
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
