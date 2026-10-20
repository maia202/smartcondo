package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    Optional<Unidade> findByBlocoAndNumero(String bloco, String numero);

    List<Unidade> findByBloco(String bloco);
}
