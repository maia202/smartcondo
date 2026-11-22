package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.AreaComum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AreaComumRequest(
        @NotBlank(message = "O nome da área comum é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
        String descricao,
        @NotNull(message = "A capacidade da área comum é obrigatória.")
        @Positive(message = "A capacidade deve ser maior que zero.")
        Integer capacidade,
        Boolean ativa) {

    public AreaComum toEntity() {
        AreaComum area = new AreaComum();
        area.setNome(nome.trim());
        area.setDescricao(descricao);
        area.setCapacidade(capacidade);
        area.setAtiva(ativa == null || ativa);
        return area;
    }
}
