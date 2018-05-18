package com.github.rygh.qq.spring;

import org.springframework.context.SmartLifecycle;

import com.github.rygh.qq.QQServer;

public class QQSpringLifecycleBean implements SmartLifecycle {

	private QQServer runtime;
	private boolean running = false;
	
	public QQSpringLifecycleBean(QQServer runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public void start() {
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
}
