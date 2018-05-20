package com.github.rygh.qq.example.rock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Rock {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private UUID id;
	private String name;

	private Rock() {
	}

	public Rock(RockSpec spec) {
		this();
		this.name = spec.getName();
	}
	
	public UUID getId() {
		return id;
	}

	public Rock setId(UUID id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Rock setName(String name) {
		this.name = name;
		return this;
	}

	public List<RockFragment> smash() {
		List<RockFragment> frags = new ArrayList<>();
		for (char c : name.toCharArray()) {
			frags.add(new RockFragment(this, c));
		}
		return frags;
	}

	@Override
	public String toString() {
		return "Rock [id=" + id + ", name=" + name + "]";
	}

}
