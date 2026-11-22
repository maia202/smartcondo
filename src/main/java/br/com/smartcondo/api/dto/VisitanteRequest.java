package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Unidade;
import br.com.smartcondo.domain.model.Visitante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record VisitanteRequest(
        @NotBlank(message = "O nome do visitante é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,
        @NotBlank(message = "O documento do visitante é obrigatório.")
        @Size(max = 30, message = "O documento deve ter no máximo 30 caracteres.")
        String documento,
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,
        @NotNull(message = "A data e hora de entrada são obrigatórias.")
        LocalDateTime dataHoraEntrada,
        LocalDateTime dataHoraSaida,
        @NotNull(message = "A unidade visitada é obrigatória.")
        Long unidadeId) {

    public Visitante toEntity(Unidade unidade) {
        Visitante visitante = new Visitante();
        visitante.setNome(nome.trim());
        visitante.setDocumento(documento.trim());
        visitante.setTelefone(telefone);
        visitante.setDataHoraEntrada(dataHoraEntrada);
        visitante.setDataHoraSaida(dataHoraSaida);
        visitante.setUnidade(unidade);
        return visitante;
    }
}
