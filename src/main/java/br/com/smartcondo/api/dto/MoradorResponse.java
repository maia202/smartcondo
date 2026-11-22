package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Morador;

import java.time.LocalDate;

public record MoradorResponse(
        Long id, String nome, String cpf, String email, String telefone, LocalDate dataCadastro, Long unidadeId) {

    public static MoradorResponse from(Morador morador) {
        return new MoradorResponse(morador.getId(), morador.getNome(), morador.getCpf(), morador.getEmail(),
                morador.getTelefone(), morador.getDataCadastro(), morador.getUnidade().getId());
    }
}
