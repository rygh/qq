package com.github.rygh.qq.example.job;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

	@Autowired
	private JobRepository repository;
	
	@GetMapping({"/", ""})
	public Stream<Job> list() {
		return repository.findAll();
	}
}
