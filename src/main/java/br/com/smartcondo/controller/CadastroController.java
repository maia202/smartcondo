package br.com.smartcondo.controller;

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public List<UnidadeResponse> listarUnidades() {
        return unidadeRepository.findAll().stream().map(CadastroController::toResponse).toList();
    }

    @GetMapping("/unidades/bloco/{bloco}")
    public List<UnidadeResponse> listarUnidadesPorBloco(@PathVariable String bloco) {
        return unidadeRepository.findByBloco(bloco.trim()).stream().map(CadastroController::toResponse).toList();
    }

    @PostMapping("/unidades")
    public ResponseEntity<UnidadeResponse> criarUnidade(@Valid @RequestBody UnidadeRequest request) {
        garantirUnidadeDisponivel(request.bloco(), request.numero(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(unidadeRepository.save(new Unidade(
                null, request.bloco().trim(), request.numero().trim(), request.areaMetrosQuadrados(), null, null))));
    }

    @PutMapping("/unidades/{id}")
    public UnidadeResponse atualizarUnidade(@PathVariable Long id, @Valid @RequestBody UnidadeRequest request) {
        Unidade unidade = buscarUnidade(id);
        garantirUnidadeDisponivel(request.bloco(), request.numero(), id);
        unidade.setBloco(request.bloco().trim());
        unidade.setNumero(request.numero().trim());
        unidade.setAreaMetrosQuadrados(request.areaMetrosQuadrados());
        return toResponse(unidadeRepository.save(unidade));
    }

    @DeleteMapping("/unidades/{id}")
    public void excluirUnidade(@PathVariable Long id) {
        unidadeRepository.delete(buscarUnidade(id));
    }

    @GetMapping("/moradores")
    public List<MoradorResponse> listarMoradores() {
        return moradorRepository.findAll().stream().map(CadastroController::toResponse).toList();
    }

    @GetMapping("/moradores/cpf/{cpf}")
    public MoradorResponse buscarMoradorPorCpf(@PathVariable String cpf) {
        Morador morador = moradorRepository.findByCpf(cpf.trim()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não encontrado."));
        return toResponse(morador);
    }

    @PostMapping("/moradores")
    public ResponseEntity<MoradorResponse> criarMorador(@Valid @RequestBody MoradorRequest request) {
        garantirCpfDisponivel(request.cpf(), null);
        Morador morador = new Morador();
        preencherMorador(morador, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(moradorRepository.save(morador)));
    }

    @PutMapping("/moradores/{id}")
    public MoradorResponse atualizarMorador(@PathVariable Long id, @Valid @RequestBody MoradorRequest request) {
        Morador morador = buscarMorador(id);
        garantirCpfDisponivel(request.cpf(), id);
        preencherMorador(morador, request);
        return toResponse(moradorRepository.save(morador));
    }

    @DeleteMapping("/moradores/{id}")
    public void excluirMorador(@PathVariable Long id) {
        moradorRepository.delete(buscarMorador(id));
    }

    @GetMapping("/areas-comuns")
    public List<AreaComumResponse> listarAreasComuns() {
        return areaComumRepository.findAll().stream().map(CadastroController::toResponse).toList();
    }

    @PostMapping("/areas-comuns")
    public ResponseEntity<AreaComumResponse> criarAreaComum(@Valid @RequestBody AreaComumRequest request) {
        AreaComum area = new AreaComum();
        preencherAreaComum(area, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(areaComumRepository.save(area)));
    }

    @PutMapping("/areas-comuns/{id}")
    public AreaComumResponse atualizarAreaComum(
            @PathVariable Long id, @Valid @RequestBody AreaComumRequest request) {
        AreaComum area = buscarAreaComum(id);
        preencherAreaComum(area, request);
        return toResponse(areaComumRepository.save(area));
    }

    @DeleteMapping("/areas-comuns/{id}")
    public void excluirAreaComum(@PathVariable Long id) {
        areaComumRepository.delete(buscarAreaComum(id));
    }

    @GetMapping("/reservas")
    public List<ReservaResponse> listarReservas() {
        return reservaRepository.findAll().stream().map(CadastroController::toResponse).toList();
    }

    @GetMapping("/reservas/morador/{moradorId}")
    public List<ReservaResponse> listarReservasPorMorador(@PathVariable Long moradorId) {
        return reservaRepository.findByMoradorId(moradorId).stream().map(CadastroController::toResponse).toList();
    }

    @GetMapping("/reservas/area-comum/{areaComumId}")
    public List<ReservaResponse> listarReservasPorAreaComum(@PathVariable Long areaComumId) {
        return reservaRepository.findByAreaComumId(areaComumId).stream()
                .map(CadastroController::toResponse).toList();
    }

    @PostMapping("/reservas")
    public ResponseEntity<ReservaResponse> criarReserva(@Valid @RequestBody ReservaRequest request) {
        validarPeriodoReserva(request.dataHoraInicio(), request.dataHoraFim());
        Reserva reserva = new Reserva();
        preencherReserva(reserva, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(reservaRepository.save(reserva)));
    }

    @PutMapping("/reservas/{id}")
    public ReservaResponse atualizarReserva(@PathVariable Long id, @Valid @RequestBody ReservaRequest request) {
        validarPeriodoReserva(request.dataHoraInicio(), request.dataHoraFim());
        Reserva reserva = buscarReserva(id);
        preencherReserva(reserva, request);
        return toResponse(reservaRepository.save(reserva));
    }

    @DeleteMapping("/reservas/{id}")
    public void excluirReserva(@PathVariable Long id) {
        reservaRepository.delete(buscarReserva(id));
    }

    @GetMapping("/visitantes")
    public List<VisitanteResponse> listarVisitantes() {
        return visitanteRepository.findAll().stream().map(CadastroController::toResponse).toList();
    }

    @GetMapping("/visitantes/unidade/{unidadeId}")
    public List<VisitanteResponse> listarVisitantesPorUnidade(@PathVariable Long unidadeId) {
        return visitanteRepository.findByUnidadeId(unidadeId).stream().map(CadastroController::toResponse).toList();
    }

    @PostMapping("/visitantes")
    public ResponseEntity<VisitanteResponse> criarVisitante(@Valid @RequestBody VisitanteRequest request) {
        validarPeriodoVisita(request.dataHoraEntrada(), request.dataHoraSaida());
        Visitante visitante = new Visitante();
        preencherVisitante(visitante, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(visitanteRepository.save(visitante)));
    }

    @PutMapping("/visitantes/{id}")
    public VisitanteResponse atualizarVisitante(
            @PathVariable Long id, @Valid @RequestBody VisitanteRequest request) {
        validarPeriodoVisita(request.dataHoraEntrada(), request.dataHoraSaida());
        Visitante visitante = buscarVisitante(id);
        preencherVisitante(visitante, request);
        return toResponse(visitanteRepository.save(visitante));
    }

    @DeleteMapping("/visitantes/{id}")
    public void excluirVisitante(@PathVariable Long id) {
        visitanteRepository.delete(buscarVisitante(id));
    }

    private void preencherMorador(Morador morador, MoradorRequest request) {
        morador.setNome(request.nome().trim());
        morador.setCpf(request.cpf().trim());
        morador.setEmail(request.email());
        morador.setTelefone(request.telefone());
        morador.setDataCadastro(request.dataCadastro() == null ? LocalDate.now() : request.dataCadastro());
        morador.setUnidade(buscarUnidade(request.unidadeId()));
    }

    private void preencherAreaComum(AreaComum area, AreaComumRequest request) {
        area.setNome(request.nome().trim());
        area.setDescricao(request.descricao());
        area.setCapacidade(request.capacidade());
        area.setAtiva(request.ativa() == null || request.ativa());
    }

    private void preencherReserva(Reserva reserva, ReservaRequest request) {
        reserva.setDataHoraInicio(request.dataHoraInicio());
        reserva.setDataHoraFim(request.dataHoraFim());
        reserva.setStatus(request.status() == null ? Reserva.StatusReserva.PENDENTE : request.status());
        reserva.setQuantidadeConvidados(request.quantidadeConvidados() == null ? 0 : request.quantidadeConvidados());
        reserva.setMorador(buscarMorador(request.moradorId()));
        reserva.setAreaComum(buscarAreaComum(request.areaComumId()));
    }

    private void preencherVisitante(Visitante visitante, VisitanteRequest request) {
        visitante.setNome(request.nome().trim());
        visitante.setDocumento(request.documento().trim());
        visitante.setTelefone(request.telefone());
        visitante.setDataHoraEntrada(request.dataHoraEntrada());
        visitante.setDataHoraSaida(request.dataHoraSaida());
        visitante.setUnidade(buscarUnidade(request.unidadeId()));
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
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada."));
    }

    private Morador buscarMorador(Long id) {
        return moradorRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Morador não encontrado."));
    }

    private AreaComum buscarAreaComum(Long id) {
        return areaComumRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Área comum não encontrada."));
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada."));
    }

    private Visitante buscarVisitante(Long id) {
        return visitanteRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Visitante não encontrado."));
    }

    private static UnidadeResponse toResponse(Unidade unidade) {
        return new UnidadeResponse(unidade.getId(), unidade.getBloco(), unidade.getNumero(),
                unidade.getAreaMetrosQuadrados());
    }

    private static MoradorResponse toResponse(Morador morador) {
        return new MoradorResponse(morador.getId(), morador.getNome(), morador.getCpf(), morador.getEmail(),
                morador.getTelefone(), morador.getDataCadastro(), morador.getUnidade().getId());
    }

    private static AreaComumResponse toResponse(AreaComum area) {
        return new AreaComumResponse(area.getId(), area.getNome(), area.getDescricao(),
                area.getCapacidade(), area.isAtiva());
    }

    private static ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getDataHoraInicio(), reserva.getDataHoraFim(),
                reserva.getStatus(), reserva.getQuantidadeConvidados(), reserva.getMorador().getId(),
                reserva.getAreaComum().getId());
    }

    private static VisitanteResponse toResponse(Visitante visitante) {
        return new VisitanteResponse(visitante.getId(), visitante.getNome(), visitante.getDocumento(),
                visitante.getTelefone(), visitante.getDataHoraEntrada(), visitante.getDataHoraSaida(),
                visitante.getUnidade().getId());
    }

    public record UnidadeRequest(
            @NotBlank @Pattern(regexp = "^(?:[1-9]|1[0-4])$", message = "Selecione um bloco entre 1 e 14.") String bloco,
            @NotBlank @Pattern(regexp = "^(?:11|12|13|14|21|22|23|24|31|32|33|34|41|42|43|44)$",
                    message = "Selecione um apartamento disponível.") String numero,
            @PositiveOrZero BigDecimal areaMetrosQuadrados) {
    }

    public record UnidadeResponse(Long id, String bloco, String numero, BigDecimal areaMetrosQuadrados) {
    }

    public record MoradorRequest(
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Size(max = 14) String cpf,
            @Email @Size(max = 160) String email,
            @Size(max = 20) String telefone,
            LocalDate dataCadastro,
            @NotNull Long unidadeId) {
    }

    public record MoradorResponse(
            Long id, String nome, String cpf, String email, String telefone, LocalDate dataCadastro, Long unidadeId) {
    }

    public record AreaComumRequest(
            @NotBlank @Size(max = 100) String nome,
            @Size(max = 500) String descricao,
            @NotNull @Positive Integer capacidade,
            Boolean ativa) {
    }

    public record AreaComumResponse(Long id, String nome, String descricao, Integer capacidade, boolean ativa) {
    }

    public record ReservaRequest(
            @NotNull LocalDateTime dataHoraInicio,
            @NotNull LocalDateTime dataHoraFim,
            Reserva.StatusReserva status,
            @PositiveOrZero Integer quantidadeConvidados,
            @NotNull Long moradorId,
            @NotNull Long areaComumId) {
    }

    public record ReservaResponse(
            Long id,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim,
            Reserva.StatusReserva status,
            Integer quantidadeConvidados,
            Long moradorId,
            Long areaComumId) {
    }

    public record VisitanteRequest(
            @NotBlank @Size(max = 120) String nome,
            @NotBlank @Size(max = 30) String documento,
            @Size(max = 20) String telefone,
            @NotNull LocalDateTime dataHoraEntrada,
            LocalDateTime dataHoraSaida,
            @NotNull Long unidadeId) {
    }

    public record VisitanteResponse(
            Long id,
            String nome,
            String documento,
            String telefone,
            LocalDateTime dataHoraEntrada,
            LocalDateTime dataHoraSaida,
            Long unidadeId) {
    }
}
