package com.alunoonline.api.service;

import com.alunoonline.api.model.Aluno;
import com.alunoonline.api.model.Disciplina;
import com.alunoonline.api.model.MatriculaAluno;
import com.alunoonline.api.model.Professor;
import com.alunoonline.api.model.dtos.NotasMatriculaDto;
import com.alunoonline.api.model.enums.StatusMatriculaAluno;
import com.alunoonline.api.repository.AlunoRepository;
import com.alunoonline.api.repository.DisciplinaRepository;
import com.alunoonline.api.repository.MatriculaAlunoRepository;
import com.alunoonline.api.repository.ProfessorRepository;
import com.alunoonline.api.service.exceptions.IntegrityException;
import com.alunoonline.api.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatriculaAlunoService {

    private static final Double GRADEAVGTOAPPROVE = 7.0;

    @Autowired
    private MatriculaAlunoRepository repository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private ProfessorRepository professorRepository;


    @Transactional
    public MatriculaAluno create(MatriculaAluno matriculaAluno){
        if(matriculaAluno.getAluno().getId() != null) {
            Aluno aluno = alunoRepository.findById(matriculaAluno.getAluno().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(matriculaAluno.getAluno().getId()));

            matriculaAluno.setAluno(aluno);
        }

        if(matriculaAluno.getDisciplina().getId() != null) {
            Disciplina disciplina = disciplinaRepository.findById(matriculaAluno.getDisciplina().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(matriculaAluno.getDisciplina().getId()));

            Professor professor = professorRepository.findById(disciplina.getProfessor().getId()).get();
            disciplina.setProfessor(professor);
            matriculaAluno.setDisciplina(disciplina);
        }

        return repository.save(matriculaAluno);
    }

    public List<MatriculaAluno> findAll() {
        return repository.findAll();
    }

    public MatriculaAluno findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public void delete(Long id) {
        try {
            MatriculaAluno matricula = findById(id);
            repository.delete(matricula);
            repository.flush();
        }
        catch (ResourceNotFoundException e){
            throw e;
        }
        catch(DataIntegrityViolationException e){
            throw new IntegrityException(id);
        }
    }

    public MatriculaAluno update(MatriculaAluno matriculaAluno, MatriculaAluno matriculaAlunoUpdated) {
        if(matriculaAlunoUpdated.getAluno().getId() != null){
            Aluno aluno = alunoRepository.findById(matriculaAlunoUpdated.getAluno().getId()).get();
            matriculaAlunoUpdated.setAluno(aluno);
        }

        if(matriculaAlunoUpdated.getDisciplina().getId() != null){
            Disciplina disciplina = disciplinaRepository.findById(matriculaAlunoUpdated.getDisciplina().getId()).get();
            matriculaAlunoUpdated.setDisciplina(disciplina);
        }

        matriculaAluno.setNota1(matriculaAlunoUpdated.getNota1());
        matriculaAluno.setNota2(matriculaAluno.getNota2());
        matriculaAluno.setAluno(matriculaAlunoUpdated.getAluno());
        matriculaAluno.setDisciplina(matriculaAlunoUpdated.getDisciplina());
        matriculaAluno.setStatus(matriculaAlunoUpdated.getStatus());

        return repository.save(matriculaAluno);
    }

    public MatriculaAluno atualizarNotas(Long matriculaAlunoId, NotasMatriculaDto notas){
        MatriculaAluno matricula = repository.findById(matriculaAlunoId).orElseThrow(() -> new ResourceNotFoundException(matriculaAlunoId));
        updateNota1(matricula, notas);
        updateNota2(matricula, notas);
        calculaMedia(matricula);
        return repository.save(matricula);
    }

    private void updateNota1(MatriculaAluno matricula, NotasMatriculaDto notas){
         matricula.setNota1(notas.getNota1() != null ? notas.getNota1() : matricula.getNota1());
    }

    private void updateNota2(MatriculaAluno matricula, NotasMatriculaDto notas){
        matricula.setNota2(notas.getNota2() != null ? notas.getNota2() : matricula.getNota2());
    }

    private void calculaMedia(MatriculaAluno matricula){
        if(matricula.getNota1() != null && matricula.getNota2() != null){
            Double somaNotas = matricula.getNota1() + matricula.getNota2();
            Double media = somaNotas / 2.0;

            if(media >= GRADEAVGTOAPPROVE){
                matricula.setStatus(StatusMatriculaAluno.APROVADO);
            }
            else{
                matricula.setStatus(StatusMatriculaAluno.REPROVADO);
            }
        }
    }
}
