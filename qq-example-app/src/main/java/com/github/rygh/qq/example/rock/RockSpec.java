package com.github.rygh.qq.example.rock;

import javax.validation.constraints.NotBlank;

public class RockSpec {
	@NotBlank
	private String name;

	public RockSpec setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return name;
	}
}
