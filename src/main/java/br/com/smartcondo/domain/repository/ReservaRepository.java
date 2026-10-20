package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByMoradorId(Long moradorId);

    List<Reserva> findByAreaComumId(Long areaComumId);
}
