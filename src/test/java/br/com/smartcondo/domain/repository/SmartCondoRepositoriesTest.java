package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.AreaComum;
import br.com.smartcondo.domain.model.Morador;
import br.com.smartcondo.domain.model.Reserva;
import br.com.smartcondo.domain.model.Unidade;
import br.com.smartcondo.domain.model.Visitante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SmartCondoRepositoriesTest {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private MoradorRepository moradorRepository;

    @Autowired
    private AreaComumRepository areaComumRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VisitanteRepository visitanteRepository;

    @Test
    void devePersistirEntidadesEConsultarRelacionamentos() {
        Unidade unidade = unidadeRepository.save(new Unidade(
                null, "A", "101", null, null, null));
        Morador morador = moradorRepository.save(new Morador(
                null, "Ana Souza", "12345678901", "ana@example.com", "11999990000",
                LocalDate.now(), unidade, null));
        AreaComum areaComum = areaComumRepository.save(new AreaComum(
                null, "Salão de festas", "Salão principal", 80, true, null));

        Reserva reserva = reservaRepository.save(new Reserva(
                null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(3),
                Reserva.StatusReserva.PENDENTE, 10, morador, areaComum));
        Visitante visitante = visitanteRepository.save(new Visitante(
                null, "Carlos Lima", "RG123456", "11988887777",
                LocalDateTime.now().plusHours(2), null, unidade));

        assertThat(unidadeRepository.findByBlocoAndNumero("A", "101")).contains(unidade);
        assertThat(moradorRepository.findByCpf("12345678901")).contains(morador);
        assertThat(areaComumRepository.findByAtivaTrue()).contains(areaComum);
        assertThat(reservaRepository.findByMoradorId(morador.getId())).contains(reserva);
        assertThat(reservaRepository.findByAreaComumId(areaComum.getId())).contains(reserva);
        assertThat(visitanteRepository.findByUnidadeId(unidade.getId())).contains(visitante);
    }
}
