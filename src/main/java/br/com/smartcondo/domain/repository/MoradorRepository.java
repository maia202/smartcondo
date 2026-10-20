package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.Morador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoradorRepository extends JpaRepository<Morador, Long> {

    Optional<Morador> findByCpf(String cpf);
}
