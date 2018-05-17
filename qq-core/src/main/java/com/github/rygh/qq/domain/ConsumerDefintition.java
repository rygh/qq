package com.github.rygh.qq.domain;

public class ConsumerDefintition {

	private String name;
	private String description;
	private String pool;
	private boolean enabled;
	
	public ConsumerDefintition(String name, String description, String pool, boolean enabled) {
		this.name = name;
		this.description = description;
		this.pool = pool;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPool() {
		return pool;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public String toString() {
		return "ConsumerDefintition ["
				+ "name=" + name 
				+ ", description=" + description 
				+ ", pool=" + pool 
				+ ", enabled=" + enabled + "]";
	}
}
