package br.com.smartcondo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExibirStatusDaAplicacaoNaApi() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacao").value("SmartCondo"))
                .andExpect(jsonPath("$.status").value("online"));
    }

    @Test
    void deveExibirTotaisDoPainel() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moradores").value(0))
                .andExpect(jsonPath("$.unidades").value(0))
                .andExpect(jsonPath("$.reservas").value(0))
                .andExpect(jsonPath("$.visitantes").value(0))
                .andExpect(jsonPath("$.areasComuns").value(0));
    }
}
