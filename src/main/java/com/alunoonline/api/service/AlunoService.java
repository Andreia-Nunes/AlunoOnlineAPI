package com.alunoonline.api.service;

import com.alunoonline.api.model.Aluno;
import com.alunoonline.api.repository.AlunoRepository;
import com.alunoonline.api.service.exceptions.IntegrityException;
import com.alunoonline.api.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    @Autowired
    AlunoRepository repository;

    @Transactional
    public Aluno create(Aluno aluno){
        return this.repository.save(aluno);
    }

    public List<Aluno> findAll(){
        return this.repository.findAll();
    }

    public Aluno findById(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public void delete(Long id){
        try {
            Aluno aluno = findById(id);
            repository.delete(aluno);
            repository.flush();
        }
        catch (ResourceNotFoundException e){
            throw e;
        }
        catch(DataIntegrityViolationException e){
            throw new IntegrityException(id);
        }
    }

    public Aluno update(Aluno aluno, Aluno alunoUpdated) {
        aluno.setNome(alunoUpdated.getNome());
        aluno.setEmail(alunoUpdated.getEmail());
        aluno.setCurso(alunoUpdated.getCurso());

        return repository.save(aluno);
    }
}
