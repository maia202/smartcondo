package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Unidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record UnidadeRequest(
        @NotBlank(message = "O bloco é obrigatório.")
        @Pattern(regexp = "^(?:[1-9]|1[0-4])$", message = "Selecione um bloco entre 1 e 14.")
        String bloco,
        @NotBlank(message = "O número do apartamento é obrigatório.")
        @Pattern(regexp = "^(?:11|12|13|14|21|22|23|24|31|32|33|34|41|42|43|44)$",
                message = "Selecione um apartamento disponível.")
        String numero,
        @PositiveOrZero(message = "A área deve ser zero ou um valor positivo.")
        BigDecimal areaMetrosQuadrados) {

    public Unidade toEntity() {
        Unidade unidade = new Unidade();
        unidade.setBloco(bloco.trim());
        unidade.setNumero(numero.trim());
        unidade.setAreaMetrosQuadrados(areaMetrosQuadrados);
        return unidade;
    }
}
