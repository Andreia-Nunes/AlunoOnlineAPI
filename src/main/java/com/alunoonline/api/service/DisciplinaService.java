package com.alunoonline.api.service;

import com.alunoonline.api.model.Disciplina;
import com.alunoonline.api.model.Professor;
import com.alunoonline.api.repository.DisciplinaRepository;
import com.alunoonline.api.repository.ProfessorRepository;
import com.alunoonline.api.service.exceptions.IntegrityException;
import com.alunoonline.api.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public Disciplina create(Disciplina disciplina){
        if(disciplina.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(disciplina.getProfessor().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(disciplina.getProfessor().getId()));

            disciplina.setProfessor(professor);
        }

        return repository.save(disciplina);
    }

    public List<Disciplina> findAll(){
        return repository.findAll();
    }

    public Disciplina findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        try {
            Disciplina disciplina = findById(id);
            repository.delete(disciplina);
            repository.flush();
        }
        catch (ResourceNotFoundException e){
            throw e;
        }
        catch(DataIntegrityViolationException e){
            throw new IntegrityException(id);
        }
    }

    public List<Disciplina> findByProfessorId(Long professorId){
        return repository.findByProfessorId(professorId);
    }

    public Disciplina update(Disciplina disciplina, Disciplina disciplinaUpdated) {
        disciplina.setNome(disciplinaUpdated.getNome());
        disciplina.setProfessor(disciplinaUpdated.getProfessor());

        return repository.save(disciplina);
    }
}
