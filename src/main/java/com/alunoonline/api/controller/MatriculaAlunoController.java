package com.alunoonline.api.controller;

import com.alunoonline.api.model.MatriculaAluno;
import com.alunoonline.api.model.dtos.HistoricoDto;
import com.alunoonline.api.model.dtos.NotasDto;
import com.alunoonline.api.service.MatriculaAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matricula-aluno")
public class MatriculaAlunoController {

    @Autowired
    private MatriculaAlunoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MatriculaAluno> create(@RequestBody MatriculaAluno matriculaAluno){
        MatriculaAluno matriculaAlunoCreated = this.service.create(matriculaAluno);
        return ResponseEntity.status(201).body(matriculaAlunoCreated);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MatriculaAluno>> findAll(){
        List<MatriculaAluno> matriculas = service.findAll();
        return ResponseEntity.status(200).body(matriculas);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MatriculaAluno> findById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.findById(id));
    }

    @GetMapping(value = "/historico-aluno/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HistoricoDto> getHistorico(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.getHistorico(id));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MatriculaAluno> update(@PathVariable Long id, @RequestBody MatriculaAluno matriculaAlunoUpdated){
        MatriculaAluno matricula = service.findById(id);
        return ResponseEntity.status(200).body(service.update(matricula, matriculaAlunoUpdated));
    }

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MatriculaAluno> atualizarNotas(@PathVariable Long id, @RequestBody NotasDto notas){
        return ResponseEntity.status(200).body(service.atualizarNotas(id, notas));
    }

    @PatchMapping(value = "/atualiza-status/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateStatusToLocked(@PathVariable Long id){
        service.updateStatusToLocked(id);
        return ResponseEntity.noContent().build();
    }
}
