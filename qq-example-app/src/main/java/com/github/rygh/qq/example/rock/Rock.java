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

	public Rock(String name) {
		this();
		this.name = name;
	}
	
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
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
