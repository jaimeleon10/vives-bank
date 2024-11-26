package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
@ExtendWith(MockitoExtension.class)
class CuentaControllerTest {

    @Mock
    private CuentaService cuentaService;

    @Mock
    private PaginationLinksUtils paginationLinksUtils;

    @InjectMocks
    private CuentaController cuentaController;

    @MockBean
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private CuentaRequest cuentaRequest;
    private CuentaRequestUpdate cuentaRequestUpdate;

    @BeforeEach
    void setUp() {
        cuentaRequest = new CuentaRequest();
        cuentaRequest.setIban("ES1234567890123456789012");
        cuentaRequest.setSaldo(new BigDecimal("1000"));
        cuentaRequest.setTipoCuenta("Cuenta Ahorros");

        cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(new BigDecimal("1500"));
    }

    @Test
    void getAllCuentas() throws Exception {
        List<CuentaResponse> cuentas = List.of(new CuentaResponse("1", "Cuenta Ahorros", new BigDecimal("1000")));
        Page<CuentaResponse> page = new PageImpl<>(cuentas);
        when(cuentaService.getAll(any(), any(), any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("link-header");

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/cuentas")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].nombre").value("Cuenta Ahorros"));

        verify(cuentaService).getAll(any(), any(), any(), any(), any());
    }

    @Test
    void getCuentaById() throws Exception {
        CuentaResponse cuentaResponse = new CuentaResponse("1", "Cuenta Ahorros", new BigDecimal("1000"));
        when(cuentaService.getById("1")).thenReturn(cuentaResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/cuentas/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Cuenta Ahorros"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("1000"));

        verify(cuentaService).getById("1");
    }

    @Test
    void saveCuenta() throws Exception {
        CuentaResponse cuentaResponse = new CuentaResponse("1", "Cuenta Ahorros", new BigDecimal("1000"));
        when(cuentaService.save(any(CuentaRequest.class))).thenReturn(cuentaResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/cuentas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("Cuenta Ahorros"));

        verify(cuentaService).save(any(CuentaRequest.class));
    }

    @Test
    void updateCuenta() throws Exception {
        CuentaResponse cuentaResponse = new CuentaResponse("1", "Cuenta Ahorros", new BigDecimal("1500"));
        when(cuentaService.update(eq("1"), any(CuentaRequestUpdate.class))).thenReturn(cuentaResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/cuentas/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.saldo").value("1500"));

        verify(cuentaService).update(eq("1"), any(CuentaRequestUpdate.class));
    }

    @Test
    void deleteCuenta() throws Exception {
        doNothing().when(cuentaService).deleteById("1");

        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/cuentas/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(cuentaService).deleteById("1");
    }
    @Test
    void handleValidationException() throws Exception {

        CuentaRequest invalidCuentaRequest = new CuentaRequest();
        invalidCuentaRequest.setIban("INVALID_IBAN");  // Iban inválido

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/v1/cuentas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidCuentaRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("El formato del IBAN es inválido"));
    }
} */