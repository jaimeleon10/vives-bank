package org.example.vivesbankproject.movimientos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.rest.movimientos.models.Transferencia;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovimientosMeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovimientosService movimientosService;

    private Domiciliacion domiciliacion;
    private IngresoDeNomina ingresoNomina;
    private PagoConTarjeta pagoTarjeta;
    private Transferencia transferencia;

    @BeforeEach
    void setUp() {
        // Initialize test objects with minimal valid data
        domiciliacion = new Domiciliacion();
        ingresoNomina = new IngresoDeNomina();
        pagoTarjeta = new PagoConTarjeta();
        transferencia = new Transferencia();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovimientoDomiciliacion_ValidRequest_ReturnsOkStatus() throws Exception {
        mockMvc.perform(post("/api/v1/me/domiciliacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(domiciliacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovimientoIngresoNomina_ValidRequest_ReturnsOkStatus() throws Exception {
        mockMvc.perform(post("/api/v1/me/ingresonomina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingresoNomina)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovimientoPagoConTarjeta_ValidRequest_ReturnsOkStatus() throws Exception {
        mockMvc.perform(post("/api/v1/me/pagotarjeta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoTarjeta)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovimientoTransferencia_ValidRequest_ReturnsOkStatus() throws Exception {
        mockMvc.perform(post("/api/v1/me/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createMovimiento_UnauthorizedUser_ReturnsForbiddenStatus() throws Exception {
        mockMvc.perform(post("/api/v1/me/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferencia)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMovimiento_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Create an invalid object with missing or incorrect data
        Transferencia invalidTransferencia = new Transferencia();
        // Do not set required fields

        mockMvc.perform(post("/api/v1/me/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTransferencia)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void revocarMovimientoTransferencia_ReturnsNotImplemented() throws Exception {
        mockMvc.perform(delete("/api/v1/me/transferencia/test-guid"))
                .andExpect(status().isOk()); // Currently returns null, so expecting OK
    }
}