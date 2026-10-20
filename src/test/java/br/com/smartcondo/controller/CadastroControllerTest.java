package br.com.smartcondo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CadastroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarConsultarAtualizarEExcluirOsCadastros() throws Exception {
        JsonNode unidade = criar("/api/unidades",
                "{\"bloco\":\"1\",\"numero\":\"11\",\"areaMetrosQuadrados\":68.5}");
        long unidadeId = unidade.get("id").asLong();

        JsonNode morador = criar("/api/moradores",
                "{\"nome\":\"Joana Lima\",\"cpf\":\"98765432100\",\"email\":\"joana@example.com\","
                        + "\"telefone\":\"11999990000\",\"unidadeId\":" + unidadeId + "}");
        long moradorId = morador.get("id").asLong();

        JsonNode area = criar("/api/areas-comuns",
                "{\"nome\":\"Salão\",\"descricao\":\"Salão principal\",\"capacidade\":60,\"ativa\":true}");
        long areaId = area.get("id").asLong();

        JsonNode reserva = criar("/api/reservas",
                "{\"dataHoraInicio\":\"2030-02-10T10:00:00\",\"dataHoraFim\":\"2030-02-10T12:00:00\","
                        + "\"quantidadeConvidados\":8,\"moradorId\":" + moradorId + ",\"areaComumId\":" + areaId + "}");
        long reservaId = reserva.get("id").asLong();
        assertThat(reserva.get("moradorId").asLong()).isEqualTo(moradorId);
        assertThat(reserva.get("areaComumId").asLong()).isEqualTo(areaId);

        JsonNode visitante = criar("/api/visitantes",
                "{\"nome\":\"Rui Costa\",\"documento\":\"RG9988\","
                        + "\"dataHoraEntrada\":\"2030-02-10T09:00:00\",\"unidadeId\":" + unidadeId + "}");
        long visitanteId = visitante.get("id").asLong();

        mockMvc.perform(get("/api/unidades")).andExpect(status().isOk());
        mockMvc.perform(get("/api/moradores")).andExpect(status().isOk());
        mockMvc.perform(get("/api/areas-comuns")).andExpect(status().isOk());
        mockMvc.perform(get("/api/reservas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/visitantes")).andExpect(status().isOk());

        MvcResult moradorPorCpf = mockMvc.perform(get("/api/moradores/cpf/{cpf}", "98765432100"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(moradorPorCpf.getResponse().getContentAsString()).get("id").asLong())
                .isEqualTo(moradorId);

        MvcResult unidadesPorBloco = mockMvc.perform(get("/api/unidades/bloco/{bloco}", "1"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(unidadesPorBloco.getResponse().getContentAsString()).size()).isEqualTo(1);

        MvcResult reservasPorMorador = mockMvc.perform(get("/api/reservas/morador/{moradorId}", moradorId))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(reservasPorMorador.getResponse().getContentAsString()).get(0)
                .get("id").asLong()).isEqualTo(reservaId);

        MvcResult reservasPorAreaComum = mockMvc.perform(get("/api/reservas/area-comum/{areaComumId}", areaId))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(reservasPorAreaComum.getResponse().getContentAsString()).get(0)
                .get("id").asLong()).isEqualTo(reservaId);

        MvcResult visitantesPorUnidade = mockMvc.perform(get("/api/visitantes/unidade/{unidadeId}", unidadeId))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(visitantesPorUnidade.getResponse().getContentAsString()).get(0)
                .get("id").asLong()).isEqualTo(visitanteId);

        MvcResult update = mockMvc.perform(put("/api/moradores/{id}", moradorId)
                        .contentType("application/json")
                        .content("{\"nome\":\"Joana Lima Atualizada\",\"cpf\":\"98765432100\","
                                + "\"email\":\"joana@example.com\",\"unidadeId\":" + unidadeId + "}"))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(objectMapper.readTree(update.getResponse().getContentAsString()).get("nome").asText())
                .isEqualTo("Joana Lima Atualizada");

        mockMvc.perform(delete("/api/visitantes/{id}", visitanteId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/reservas/{id}", reservaId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/moradores/{id}", moradorId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/areas-comuns/{id}", areaId)).andExpect(status().isOk());
        mockMvc.perform(delete("/api/unidades/{id}", unidadeId)).andExpect(status().isOk());
    }

    @Test
    void deveRecusarReservaComPeriodoInvalido() throws Exception {
        mockMvc.perform(post("/api/reservas")
                        .contentType("application/json")
                        .content("{\"dataHoraInicio\":\"2030-02-10T12:00:00\","
                                + "\"dataHoraFim\":\"2030-02-10T10:00:00\","
                                + "\"moradorId\":1,\"areaComumId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarBlocoEApartamentoForaDasOpcoesDisponiveis() throws Exception {
        mockMvc.perform(post("/api/unidades")
                        .contentType("application/json")
                        .content("{\"bloco\":\"15\",\"numero\":\"45\"}"))
                .andExpect(status().isBadRequest());
    }

    private JsonNode criar(String caminho, String conteudo) throws Exception {
        MvcResult resultado = mockMvc.perform(post(caminho)
                        .contentType("application/json")
                        .content(conteudo))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString());
    }
}
