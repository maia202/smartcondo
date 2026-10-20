package br.com.smartcondo.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "unidades", uniqueConstraints = {
        @UniqueConstraint(name = "uk_unidade_bloco_numero", columnNames = {"bloco", "numero"})
})
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String bloco;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String numero;

    @PositiveOrZero
    @Column(name = "area_metros_quadrados", precision = 8, scale = 2)
    private BigDecimal areaMetrosQuadrados;

    @OneToMany(mappedBy = "unidade")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Morador> moradores = new ArrayList<>();

    @OneToMany(mappedBy = "unidade")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Visitante> visitantes = new ArrayList<>();
}
