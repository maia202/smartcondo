package br.com.smartcondo.controller;

import br.com.smartcondo.api.dto.AreaComumRequest;
import br.com.smartcondo.api.dto.AreaComumResponse;
import br.com.smartcondo.api.dto.MoradorRequest;
import br.com.smartcondo.api.dto.MoradorResponse;
import br.com.smartcondo.api.dto.ReservaRequest;
import br.com.smartcondo.api.dto.ReservaResponse;
import br.com.smartcondo.api.dto.UnidadeRequest;
import br.com.smartcondo.api.dto.UnidadeResponse;
import br.com.smartcondo.api.dto.VisitanteRequest;
import br.com.smartcondo.api.dto.VisitanteResponse;
import br.com.smartcondo.domain.model.AreaComum;
import br.com.smartcondo.domain.model.Morador;
import br.com.smartcondo.domain.model.Reserva;
import br.com.smartcondo.domain.model.Unidade;
import br.com.smartcondo.domain.model.Visitante;
import br.com.smartcondo.domain.repository.AreaComumRepository;
import br.com.smartcondo.domain.repository.MoradorRepository;
import br.com.smartcondo.domain.repository.ReservaRepository;
import br.com.smartcondo.domain.repository.UnidadeRepository;
import br.com.smartcondo.domain.repository.VisitanteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CadastroController {

    private final MoradorRepository moradorRepository;
    private final UnidadeRepository unidadeRepository;
    private final ReservaRepository reservaRepository;
    private final VisitanteRepository visitanteRepository;
    private final AreaComumRepository areaComumRepository;

    public CadastroController(
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

    @GetMapping("/unidades")
    public ResponseEntity<List<UnidadeResponse>> listarUnidades() {
        return ResponseEntity.ok(unidadeRepository.findAll().stream().map(UnidadeResponse::from).toList());
    }

    @GetMapping("/unidades/bloco/{bloco}")
    public ResponseEntity<List<UnidadeResponse>> listarUnidadesPorBloco(@PathVariable String bloco) {
        return ResponseEntity.ok(unidadeRepository.findByBloco(bloco.trim()).stream()
                .map(UnidadeResponse::from).toList());
    }

    @GetMapping("/unidades/{id}")
    public ResponseEntity<UnidadeResponse> buscarUnidadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(UnidadeResponse.from(buscarUnidade(id)));
    }

    @PostMapping("/unidades")
    public ResponseEntity<UnidadeResponse> criarUnidade(@Valid @RequestBody UnidadeRequest request) {
        Unidade unidade = request.toEntity();
        garantirUnidadeDisponivel(unidade.getBloco(), unidade.getNumero(), null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnidadeResponse.from(unidadeRepository.save(unidade)));
    }

    @PutMapping("/unidades/{id}")
    public ResponseEntity<UnidadeResponse> atualizarUnidade(
            @PathVariable Long id, @Valid @RequestBody UnidadeRequest request) {
        Unidade unidade = buscarUnidade(id);
        Unidade dados = request.toEntity();
        garantirUnidadeDisponivel(dados.getBloco(), dados.getNumero(), id);
        unidade.setBloco(dados.getBloco());
        unidade.setNumero(dados.getNumero());
        unidade.setAreaMetrosQuadrados(dados.getAreaMetrosQuadrados());
        return ResponseEntity.ok(UnidadeResponse.from(unidadeRepository.save(unidade)));
    }

    @DeleteMapping("/unidades/{id}")
    public ResponseEntity<Void> excluirUnidade(@PathVariable Long id) {
        unidadeRepository.delete(buscarUnidade(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/moradores")
    public ResponseEntity<List<MoradorResponse>> listarMoradores() {
        return ResponseEntity.ok(moradorRepository.findAll().stream().map(MoradorResponse::from).toList());
    }

    @GetMapping("/moradores/{id}")
    public ResponseEntity<MoradorResponse> buscarMoradorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(MoradorResponse.from(buscarMorador(id)));
    }

    @GetMapping("/moradores/cpf/{cpf}")
    public ResponseEntity<MoradorResponse> buscarMoradorPorCpf(@PathVariable String cpf) {
        Morador morador = moradorRepository.findByCpf(cpf.trim()).orElseThrow(() ->
                new NoSuchElementException("Morador não encontrado."));
        return ResponseEntity.ok(MoradorResponse.from(morador));
    }

    @PostMapping("/moradores")
    public ResponseEntity<MoradorResponse> criarMorador(@Valid @RequestBody MoradorRequest request) {
        garantirCpfDisponivel(request.cpf(), null);
        Morador morador = request.toEntity(buscarUnidade(request.unidadeId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MoradorResponse.from(moradorRepository.save(morador)));
    }

    @PutMapping("/moradores/{id}")
    public ResponseEntity<MoradorResponse> atualizarMorador(
            @PathVariable Long id, @Valid @RequestBody MoradorRequest request) {
        Morador morador = buscarMorador(id);
        garantirCpfDisponivel(request.cpf(), id);
        Morador dados = request.toEntity(buscarUnidade(request.unidadeId()));
        morador.setNome(dados.getNome());
        morador.setCpf(dados.getCpf());
        morador.setEmail(dados.getEmail());
        morador.setTelefone(dados.getTelefone());
        morador.setDataCadastro(dados.getDataCadastro());
        morador.setUnidade(dados.getUnidade());
        return ResponseEntity.ok(MoradorResponse.from(moradorRepository.save(morador)));
    }

    @DeleteMapping("/moradores/{id}")
    public ResponseEntity<Void> excluirMorador(@PathVariable Long id) {
        moradorRepository.delete(buscarMorador(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/areas-comuns")
    public ResponseEntity<List<AreaComumResponse>> listarAreasComuns() {
        return ResponseEntity.ok(areaComumRepository.findAll().stream().map(AreaComumResponse::from).toList());
    }

    @GetMapping("/areas-comuns/{id}")
    public ResponseEntity<AreaComumResponse> buscarAreaComumPorId(@PathVariable Long id) {
        return ResponseEntity.ok(AreaComumResponse.from(buscarAreaComum(id)));
    }

    @PostMapping("/areas-comuns")
    public ResponseEntity<AreaComumResponse> criarAreaComum(@Valid @RequestBody AreaComumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AreaComumResponse.from(areaComumRepository.save(request.toEntity())));
    }

    @PutMapping("/areas-comuns/{id}")
    public ResponseEntity<AreaComumResponse> atualizarAreaComum(
            @PathVariable Long id, @Valid @RequestBody AreaComumRequest request) {
        AreaComum area = buscarAreaComum(id);
        AreaComum dados = request.toEntity();
        area.setNome(dados.getNome());
        area.setDescricao(dados.getDescricao());
        area.setCapacidade(dados.getCapacidade());
        area.setAtiva(dados.isAtiva());
        return ResponseEntity.ok(AreaComumResponse.from(areaComumRepository.save(area)));
    }

    @DeleteMapping("/areas-comuns/{id}")
    public ResponseEntity<Void> excluirAreaComum(@PathVariable Long id) {
        areaComumRepository.delete(buscarAreaComum(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaResponse>> listarReservas() {
        return ResponseEntity.ok(reservaRepository.findAll().stream().map(ReservaResponse::from).toList());
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<ReservaResponse> buscarReservaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ReservaResponse.from(buscarReserva(id)));
    }

    @GetMapping("/reservas/morador/{moradorId}")
    public ResponseEntity<List<ReservaResponse>> listarReservasPorMorador(@PathVariable Long moradorId) {
        return ResponseEntity.ok(reservaRepository.findByMoradorId(moradorId).stream()
                .map(ReservaResponse::from).toList());
    }

    @GetMapping("/reservas/area-comum/{areaComumId}")
    public ResponseEntity<List<ReservaResponse>> listarReservasPorAreaComum(@PathVariable Long areaComumId) {
        return ResponseEntity.ok(reservaRepository.findByAreaComumId(areaComumId).stream()
                .map(ReservaResponse::from).toList());
    }

    @PostMapping("/reservas")
    public ResponseEntity<ReservaResponse> criarReserva(@Valid @RequestBody ReservaRequest request) {
        validarPeriodoReserva(request.dataHoraInicio(), request.dataHoraFim());
        Reserva reserva = request.toEntity(buscarMorador(request.moradorId()),
                buscarAreaComum(request.areaComumId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservaResponse.from(reservaRepository.save(reserva)));
    }

    @PutMapping("/reservas/{id}")
    public ResponseEntity<ReservaResponse> atualizarReserva(
            @PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        validarPeriodoReserva(request.dataHoraInicio(), request.dataHoraFim());
        Reserva reserva = buscarReserva(id);
        Reserva dados = request.toEntity(buscarMorador(request.moradorId()),
                buscarAreaComum(request.areaComumId()));
        reserva.setDataHoraInicio(dados.getDataHoraInicio());
        reserva.setDataHoraFim(dados.getDataHoraFim());
        reserva.setStatus(dados.getStatus());
        reserva.setQuantidadeConvidados(dados.getQuantidadeConvidados());
        reserva.setMorador(dados.getMorador());
        reserva.setAreaComum(dados.getAreaComum());
        return ResponseEntity.ok(ReservaResponse.from(reservaRepository.save(reserva)));
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<Void> excluirReserva(@PathVariable Long id) {
        reservaRepository.delete(buscarReserva(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/visitantes")
    public ResponseEntity<List<VisitanteResponse>> listarVisitantes() {
        return ResponseEntity.ok(visitanteRepository.findAll().stream().map(VisitanteResponse::from).toList());
    }

    @GetMapping("/visitantes/{id}")
    public ResponseEntity<VisitanteResponse> buscarVisitantePorId(@PathVariable Long id) {
        return ResponseEntity.ok(VisitanteResponse.from(buscarVisitante(id)));
    }

    @GetMapping("/visitantes/unidade/{unidadeId}")
    public ResponseEntity<List<VisitanteResponse>> listarVisitantesPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(visitanteRepository.findByUnidadeId(unidadeId).stream()
                .map(VisitanteResponse::from).toList());
    }

    @PostMapping("/visitantes")
    public ResponseEntity<VisitanteResponse> criarVisitante(@Valid @RequestBody VisitanteRequest request) {
        validarPeriodoVisita(request.dataHoraEntrada(), request.dataHoraSaida());
        Visitante visitante = request.toEntity(buscarUnidade(request.unidadeId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(VisitanteResponse.from(visitanteRepository.save(visitante)));
    }

    @PutMapping("/visitantes/{id}")
    public ResponseEntity<VisitanteResponse> atualizarVisitante(
            @PathVariable Long id, @Valid @RequestBody VisitanteRequest request) {
        validarPeriodoVisita(request.dataHoraEntrada(), request.dataHoraSaida());
        Visitante visitante = buscarVisitante(id);
        Visitante dados = request.toEntity(buscarUnidade(request.unidadeId()));
        visitante.setNome(dados.getNome());
        visitante.setDocumento(dados.getDocumento());
        visitante.setTelefone(dados.getTelefone());
        visitante.setDataHoraEntrada(dados.getDataHoraEntrada());
        visitante.setDataHoraSaida(dados.getDataHoraSaida());
        visitante.setUnidade(dados.getUnidade());
        return ResponseEntity.ok(VisitanteResponse.from(visitanteRepository.save(visitante)));
    }

    @DeleteMapping("/visitantes/{id}")
    public ResponseEntity<Void> excluirVisitante(@PathVariable Long id) {
        visitanteRepository.delete(buscarVisitante(id));
        return ResponseEntity.noContent().build();
    }

    private void garantirCpfDisponivel(String cpf, Long idAtual) {
        moradorRepository.findByCpf(cpf.trim())
                .filter(morador -> !morador.getId().equals(idAtual))
                .ifPresent(morador -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um morador com este CPF.");
                });
    }

    private void garantirUnidadeDisponivel(String bloco, String numero, Long idAtual) {
        unidadeRepository.findByBlocoAndNumero(bloco.trim(), numero.trim())
                .filter(unidade -> !unidade.getId().equals(idAtual))
                .ifPresent(unidade -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, "Já existe uma unidade com este bloco e número.");
                });
    }

    private static void validarPeriodoReserva(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O fim da reserva deve ser posterior ao início.");
        }
    }

    private static void validarPeriodoVisita(LocalDateTime entrada, LocalDateTime saida) {
        if (saida != null && !saida.isAfter(entrada)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A saída do visitante deve ser posterior à entrada.");
        }
    }

    private Unidade buscarUnidade(Long id) {
        return unidadeRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Unidade não encontrada."));
    }

    private Morador buscarMorador(Long id) {
        return moradorRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Morador não encontrado."));
    }

    private AreaComum buscarAreaComum(Long id) {
        return areaComumRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Área comum não encontrada."));
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Reserva não encontrada."));
    }

    private Visitante buscarVisitante(Long id) {
        return visitanteRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Visitante não encontrado."));
    }
}
