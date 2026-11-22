package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Reserva;

import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Reserva.StatusReserva status,
        Integer quantidadeConvidados,
        Long moradorId,
        Long areaComumId) {

    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getDataHoraInicio(), reserva.getDataHoraFim(),
                reserva.getStatus(), reserva.getQuantidadeConvidados(), reserva.getMorador().getId(),
                reserva.getAreaComum().getId());
    }
}
