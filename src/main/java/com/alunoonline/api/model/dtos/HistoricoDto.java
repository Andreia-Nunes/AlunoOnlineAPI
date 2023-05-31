package com.alunoonline.api.model.dtos;

import lombok.Data;
import java.util.List;

@Data
public class HistoricoDto {
    private String nomeAluno;
    private String curso;
    private List<DisciplinasDto> disciplinas;
}
