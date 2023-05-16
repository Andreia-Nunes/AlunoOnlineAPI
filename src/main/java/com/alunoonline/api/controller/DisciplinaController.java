package com.alunoonline.api.controller;

import com.alunoonline.api.model.Disciplina;
import com.alunoonline.api.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Disciplina> create(@RequestBody Disciplina disciplina){
        Disciplina disciplinaCreated = service.create(disciplina);
        return ResponseEntity.status(201).body(disciplinaCreated);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Disciplina>> findAll(){
        List<Disciplina> disciplinas = service.findAll();
        return ResponseEntity.status(200).body(disciplinas);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Disciplina> findById(@PathVariable Long id){
        return ResponseEntity.status(200).body(service.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Disciplina disciplinaUpdated){
        Disciplina disciplina = service.findById(id);
        return ResponseEntity.status(200).body(service.update(disciplina, disciplinaUpdated));
    }

    @GetMapping(value = "/professor/{professorId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Disciplina>> findByProfessorId(@PathVariable Long professorId){
        List<Disciplina> disciplinas = service.findByProfessorId(professorId);
        return ResponseEntity.status(200).body(disciplinas);
    }


}
