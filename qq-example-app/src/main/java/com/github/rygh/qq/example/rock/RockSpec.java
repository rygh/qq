package com.github.rygh.qq.example.rock;

import javax.validation.constraints.NotBlank;

public class RockSpec {
	@NotBlank
	private String name;

	public String getName() {
		return name;
	}
}
