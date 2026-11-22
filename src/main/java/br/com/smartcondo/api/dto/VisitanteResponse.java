package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Visitante;

import java.time.LocalDateTime;

public record VisitanteResponse(
        Long id,
        String nome,
        String documento,
        String telefone,
        LocalDateTime dataHoraEntrada,
        LocalDateTime dataHoraSaida,
        Long unidadeId) {

    public static VisitanteResponse from(Visitante visitante) {
        return new VisitanteResponse(visitante.getId(), visitante.getNome(), visitante.getDocumento(),
                visitante.getTelefone(), visitante.getDataHoraEntrada(), visitante.getDataHoraSaida(),
                visitante.getUnidade().getId());
    }
}
