package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Unidade;

import java.math.BigDecimal;

public record UnidadeResponse(Long id, String bloco, String numero, BigDecimal areaMetrosQuadrados) {

    public static UnidadeResponse from(Unidade unidade) {
        return new UnidadeResponse(unidade.getId(), unidade.getBloco(), unidade.getNumero(),
                unidade.getAreaMetrosQuadrados());
    }
}
