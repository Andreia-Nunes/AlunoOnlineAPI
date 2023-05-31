package com.alunoonline.api.service;

import com.alunoonline.api.model.Aluno;
import com.alunoonline.api.model.Disciplina;
import com.alunoonline.api.model.MatriculaAluno;
import com.alunoonline.api.model.Professor;
import com.alunoonline.api.model.dtos.DisciplinasDto;
import com.alunoonline.api.model.dtos.HistoricoDto;
import com.alunoonline.api.model.dtos.NotasDto;
import com.alunoonline.api.model.enums.StatusMatriculaAluno;
import com.alunoonline.api.repository.AlunoRepository;
import com.alunoonline.api.repository.DisciplinaRepository;
import com.alunoonline.api.repository.MatriculaAlunoRepository;
import com.alunoonline.api.repository.ProfessorRepository;
import com.alunoonline.api.service.exceptions.IntegrityException;
import com.alunoonline.api.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
        matriculaAluno.setNota2(matriculaAlunoUpdated.getNota2());
        matriculaAluno.setAluno(matriculaAlunoUpdated.getAluno());
        matriculaAluno.setDisciplina(matriculaAlunoUpdated.getDisciplina());
        matriculaAluno.setStatus(matriculaAlunoUpdated.getStatus());

        return repository.save(matriculaAluno);
    }

    public MatriculaAluno atualizarNotas(Long matriculaAlunoId, NotasDto notas){
        MatriculaAluno matricula = repository.findById(matriculaAlunoId).orElseThrow(() -> new ResourceNotFoundException(matriculaAlunoId));
        updateNota1(matricula, notas);
        updateNota2(matricula, notas);
        calculaMedia(matricula);
        return repository.save(matricula);
    }

    private void updateNota1(MatriculaAluno matricula, NotasDto notas){
         matricula.setNota1(notas.getNota1() != null ? notas.getNota1() : matricula.getNota1());
    }

    private void updateNota2(MatriculaAluno matricula, NotasDto notas){
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

    public void updateStatusToLocked(Long matriculaId){
        MatriculaAluno matricula = repository.findById(matriculaId).orElseThrow(() -> new ResourceNotFoundException(matriculaId));

        if(lockIsAllowed(matricula)){
            matricula.setStatus(StatusMatriculaAluno.TRANCADO);
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Só é possível trancar uma matrícula com status MATRICULADO.");
        }

        repository.save(matricula);
    }

    private boolean lockIsAllowed(MatriculaAluno matricula){
        return matricula.getStatus().equals(StatusMatriculaAluno.MATRICULADO) ? true : false;
    }

    public HistoricoDto getHistorico(Long alunoId){
        List<MatriculaAluno> matriculas = repository.findByAlunoId(alunoId);

        if(!matriculas.isEmpty()){
            HistoricoDto historicoDto = new HistoricoDto();

            historicoDto.setNomeAluno(matriculas.get(0).getAluno().getNome());
            historicoDto.setCurso(matriculas.get(0).getAluno().getCurso());

            List<DisciplinasDto> disciplinasList = new ArrayList<>();

            for(MatriculaAluno matricula : matriculas){
                DisciplinasDto disciplinaDto = new DisciplinasDto();

                disciplinaDto.setNomeDisciplina(matricula.getDisciplina().getNome());
                disciplinaDto.setNomeProfessor(matricula.getDisciplina().getProfessor().getNome());
                disciplinaDto.setNota1(matricula.getNota1());
                disciplinaDto.setNota2(matricula.getNota2());

                if(matricula.getNota1() != null && matricula.getNota2() != null){
                    disciplinaDto.setMedia((matricula.getNota1() + matricula.getNota2()) / 2.0);
                } else{
                    disciplinaDto.setMedia(null);
                }

                disciplinaDto.setStatus(matricula.getStatus());

                disciplinasList.add(disciplinaDto);
            }

            historicoDto.setDisciplinas(disciplinasList);
            return historicoDto;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse aluno não possui matrículas");
    }
}
