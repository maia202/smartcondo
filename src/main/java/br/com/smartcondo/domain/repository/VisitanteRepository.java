package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitanteRepository extends JpaRepository<Visitante, Long> {

    List<Visitante> findByUnidadeId(Long unidadeId);
}
