package br.com.smartcondo.controller;

import br.com.smartcondo.domain.repository.AreaComumRepository;
import br.com.smartcondo.domain.repository.MoradorRepository;
import br.com.smartcondo.domain.repository.ReservaRepository;
import br.com.smartcondo.domain.repository.UnidadeRepository;
import br.com.smartcondo.domain.repository.VisitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatusController {

    private final MoradorRepository moradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final ReservaRepository reservaRepository;
    private final VisitanteRepository visitanteRepository;
    private final AreaComumRepository areaComumRepository;

    @Autowired
    public StatusController(
            MoradorRepository moradorRepository,
            UnidadeRepository unidadeRepository,
            ReservaRepository reservaRepository,
            VisitanteRepository visitanteRepository,
            AreaComumRepository areaComumRepository) {
        this.moradorRepository = moradorRepository;
        this.unidadeRepository = unidadeRepository;
        this.reservaRepository = reservaRepository;
        this.visitanteRepository = visitanteRepository;
        this.areaComumRepository = areaComumRepository;
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {
        return Map.of(
                "aplicacao", "SmartCondo",
                "status", "online",
                "mensagem", "Aplicacao de gestao condominial em execucao"
        );
    }

    @GetMapping("/api/dashboard")
    public Map<String, Long> dashboard() {
        return Map.of(
                "moradores", moradorRepository.count(),
                "unidades", unidadeRepository.count(),
                "reservas", reservaRepository.count(),
                "visitantes", visitanteRepository.count(),
                "areasComuns", areaComumRepository.count()
        );
    }
}
