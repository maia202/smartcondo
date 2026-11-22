package br.com.smartcondo.api.dto;

import br.com.smartcondo.domain.model.Morador;
import br.com.smartcondo.domain.model.Unidade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MoradorRequest(
        @NotBlank(message = "O nome do morador é obrigatório.") @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,
        @NotBlank(message = "O CPF do morador é obrigatório.") @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres.")
        String cpf,
        @Email(message = "Informe um endereço de e-mail válido.") @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres.")
        String email,
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,
        LocalDate dataCadastro,
        @NotNull(message = "A unidade do morador é obrigatória.")
        Long unidadeId) {

    public Morador toEntity(Unidade unidade) {
        Morador morador = new Morador();
        morador.setNome(nome.trim());
        morador.setCpf(cpf.trim());
        morador.setEmail(email);
        morador.setTelefone(telefone);
        morador.setDataCadastro(dataCadastro == null ? LocalDate.now() : dataCadastro);
        morador.setUnidade(unidade);
        return morador;
    }
}
