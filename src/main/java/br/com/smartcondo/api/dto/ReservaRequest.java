package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.AreaComum;
import br.com.smartcondo.domain.model.Morador;
import br.com.smartcondo.domain.model.Reserva;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record ReservaRequest(
        @NotNull(message = "A data e hora de início são obrigatórias.")
        LocalDateTime dataHoraInicio,
        @NotNull(message = "A data e hora de término são obrigatórias.")
        LocalDateTime dataHoraFim,
        Reserva.StatusReserva status,
        @PositiveOrZero(message = "A quantidade de convidados deve ser zero ou maior.")
        Integer quantidadeConvidados,
        @NotNull(message = "O morador responsável pela reserva é obrigatório.")
        Long moradorId,
        @NotNull(message = "A área comum da reserva é obrigatória.")
        Long areaComumId) {

    public Reserva toEntity(Morador morador, AreaComum areaComum) {
        Reserva reserva = new Reserva();
        reserva.setDataHoraInicio(dataHoraInicio);
        reserva.setDataHoraFim(dataHoraFim);
        reserva.setStatus(status == null ? Reserva.StatusReserva.PENDENTE : status);
        reserva.setQuantidadeConvidados(quantidadeConvidados == null ? 0 : quantidadeConvidados);
        reserva.setMorador(morador);
        reserva.setAreaComum(areaComum);
        return reserva;
    }
}
