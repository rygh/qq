package com.github.rygh.qq.example.rock;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RockFragment {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private UUID id;
	private String letter;
	
	@ManyToOne
	private Rock parent;
	
	private RockFragment() {
	}
	
	public RockFragment(Rock parent, char c) {
		this();
		this.parent = parent;
		this.letter = String.valueOf(c);
	}

	public UUID getId() {
		return id;
	}

	public String getLetter() {
		return letter;
	}

	public Rock getParent() {
		return parent;
	}

}
