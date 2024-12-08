package org.example.vivesbankproject.movimientos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosService;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.*;
import org.example.vivesbankproject.rest.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MovimientosMeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovimientosService movimientosService;

    @InjectMocks
    private MovimientosMeController movimientosMeController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movimientosMeController)
                .setControllerAdvice(new ExceptionHandlerExceptionResolver())
                .build();

        mockUser = new User();
        mockUser.setId(1L);
    }

    @Test
    void testCreateMovimientoDomiciliacion() throws Exception {
        Domiciliacion domiciliacion = new Domiciliacion();
        domiciliacion.setCantidad(BigDecimal.valueOf(100.0));

        when(movimientosService.saveDomiciliacion(any(User.class), any(Domiciliacion.class)))
                .thenReturn(domiciliacion);

        mockMvc.perform(post("/api/v1/me/domiciliacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100.0}")
                        .principal(() -> mockUser.getGuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(movimientosService).saveDomiciliacion(any(User.class), any(Domiciliacion.class));
    }

    @Test
    void testCreateMovimientoIngresoNomina() throws Exception {
        IngresoDeNomina ingresoNomina = new IngresoDeNomina();
        ingresoNomina.setCantidad(2000.0);

        when(movimientosService.saveIngresoDeNomina(any(User.class), any(IngresoDeNomina.class)))
                .thenReturn(new MovimientoResponse());

        mockMvc.perform(post("/api/v1/me/ingresonomina")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 2000.0}")
                        .principal(() -> mockUser.getGuid()))
                .andExpect(status().isOk());

        verify(movimientosService).saveIngresoDeNomina(any(User.class), any(IngresoDeNomina.class));
    }

    @Test
    void testCreateMovimientoPagoConTarjeta() throws Exception {
        PagoConTarjeta pagoTarjeta = new PagoConTarjeta();
        pagoTarjeta.setCantidad(50.0);

        when(movimientosService.savePagoConTarjeta(any(User.class), any(PagoConTarjeta.class)))
                .thenReturn(new MovimientoResponse());

        mockMvc.perform(post("/api/v1/me/pagotarjeta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.0}")
                        .principal(() -> mockUser.getGuid()))
                .andExpect(status().isOk());

        verify(movimientosService).savePagoConTarjeta(any(User.class), any(PagoConTarjeta.class));
    }

    @Test
    void testCreateMovimientoTransferencia() throws Exception {
        Transferencia transferencia = new Transferencia();
        transferencia.setCantidad(BigDecimal.valueOf(500.0));

        when(movimientosService.saveTransferencia(any(User.class), any(Transferencia.class)))
                .thenReturn(new MovimientoResponse());

        mockMvc.perform(post("/api/v1/me/transferencia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 500.0}")
                        .principal(() -> mockUser.getGuid()))
                .andExpect(status().isOk());

        verify(movimientosService).saveTransferencia(any(User.class), any(Transferencia.class));
    }

    @Test
    void testRevocarMovimientoTransferencia() throws Exception {
        mockMvc.perform(delete("/api/v1/me/transferencia/someGuid")
                        .principal(() -> mockUser.getGuid()))
                .andExpect(status().isOk());
    }
}