package com.github.rygh.qq.example.services;

import java.util.List;
import java.util.stream.Collectors;

public class Rock {
	private Long id;
	private String name;
	
	public List<RockFragment> smash() {
		return name.chars()
			.mapToObj(Character.class::cast)
			.map(RockFragment::new)
			.collect(Collectors.toList());
	}
}
