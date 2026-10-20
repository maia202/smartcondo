package br.com.smartcondo.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "visitantes")
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String documento;

    @Column(length = 20)
    private String telefone;

    @NotNull
    @Column(name = "data_hora_entrada", nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Unidade unidade;
}
