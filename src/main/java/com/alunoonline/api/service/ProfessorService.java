package com.alunoonline.api.service;

import com.alunoonline.api.model.Professor;
import com.alunoonline.api.repository.ProfessorRepository;
import com.alunoonline.api.service.exceptions.IntegrityException;
import com.alunoonline.api.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository repository;

    @Transactional
    public Professor create(Professor professor){
        return repository.save(professor);
    }

    public List<Professor> findAll() {
        return repository.findAll();
    }

    public Professor findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        try {
            Professor professor = findById(id);
            repository.delete(professor);
            repository.flush();
        }
        catch (ResourceNotFoundException e){
            throw e;
        }
        catch(DataIntegrityViolationException e){
            throw new IntegrityException(id);
        }
    }

    public Professor update(Professor professor, Professor professorUpdated) {
        professor.setNome(professorUpdated.getNome());
        professor.setEmail(professorUpdated.getEmail());

        return repository.save(professor);
    }
}
