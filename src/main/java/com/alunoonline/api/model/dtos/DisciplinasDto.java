package com.alunoonline.api.model.dtos;

import com.alunoonline.api.model.enums.StatusMatriculaAluno;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class DisciplinasDto {
    private String nomeDisciplina;
    private String nomeProfessor;
    private Double nota1;
    private Double nota2;
    private Double media;

    @Enumerated(EnumType.STRING)
    private StatusMatriculaAluno status;
}
