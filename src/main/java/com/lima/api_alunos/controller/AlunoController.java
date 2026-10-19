package com.lima.api_alunos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lima.api_alunos.dto.AlunoRequest;
import com.lima.api_alunos.dto.AlunoResponse;
import com.lima.api_alunos.service.AlunoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

	private final AlunoService service;

	public AlunoController(AlunoService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<List<AlunoResponse>> listarAlunos() {
		return ResponseEntity.status(HttpStatus.OK).body(service.listarAlunos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AlunoResponse> obterAlunoPorId(@PathVariable int id) {
		return ResponseEntity.status(HttpStatus.OK).body(service.obterAlunoPorId(id));
	}

	@PostMapping
	public ResponseEntity<AlunoResponse> cadastrarAluno(@Valid @RequestBody AlunoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarAluno(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AlunoResponse> atualizarAluno(@PathVariable int id,
			@Valid @RequestBody AlunoRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(service.atualizarAluno(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirAluno(@PathVariable int id) {
		service.excluirAluno(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
