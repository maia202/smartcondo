package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.AreaComum;

public record AreaComumResponse(Long id, String nome, String descricao, Integer capacidade, boolean ativa) {

    public static AreaComumResponse from(AreaComum area) {
        return new AreaComumResponse(area.getId(), area.getNome(), area.getDescricao(),
                area.getCapacidade(), area.isAtiva());
    }
}
