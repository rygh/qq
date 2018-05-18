package com.github.rygh.qq.example.services;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.rygh.qq.annotations.QQPublish;

@RestController
@RequestMapping(path = "/api/rocks")
public class ReceiveRockController {

	private static final Logger logger = LoggerFactory.getLogger(ReceiveRockController.class);
	
	@QQPublish("rock-splitter")
	private RockSplittingService rockSplittingService;
	
	@PostMapping
	public ResponseEntity<?> createRock(Rock rock) {
		
		
		return ResponseEntity.created(URI.create("/api/rocks/123")).body(rock);
	}
	
}
