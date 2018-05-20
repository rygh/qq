package com.github.rygh.qq.example.rock;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.rygh.qq.annotations.QQPublish;

@RestController
@RequestMapping(path = "/api/rocks")
public class ReceiveRockController {

	private final RockSplittingService rockSplittingService;
	private final RockRepository repository;
	
	public ReceiveRockController(@QQPublish("rock-splitter") RockSplittingService rockService, RockRepository repository) {
		this.rockSplittingService = rockService;
		this.repository = repository;
	}
	
	@PostMapping({"/", ""})
	@Transactional
	public ResponseEntity<Rock> createRock(@RequestBody @Valid RockSpec spec) {
		Rock rock = repository.save(new Rock(spec.getName()));
		rockSplittingService.smash(rock);
		return ResponseEntity.created(URI.create("/api/rocks/" + rock.getId()))
			.body(rock);
	}
	
	@GetMapping({"/", ""}) 
	public Iterable<Rock> list() {
		return repository.findAll();
	}
	
	@GetMapping("/{id}")
	public Optional<Rock> showRock(@PathVariable UUID id) {
		return repository.findById(id);
	}
}
