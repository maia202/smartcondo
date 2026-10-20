package br.com.smartcondo.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @NotNull
    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status = StatusReserva.PENDENTE;

    @PositiveOrZero
    @Column(name = "quantidade_convidados", nullable = false)
    private Integer quantidadeConvidados = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "morador_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Morador morador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_comum_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AreaComum areaComum;

    public enum StatusReserva {
        PENDENTE,
        CONFIRMADA,
        CANCELADA
    }
}
