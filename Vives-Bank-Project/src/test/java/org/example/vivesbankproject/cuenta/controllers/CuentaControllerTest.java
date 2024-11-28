package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class CuentaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String myEndpoint = "/v1/cuentas";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuentaService cuentaService;

    @MockBean
    private PaginationLinksUtils paginationLinksUtils;

    @Test
    void GetAll() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("unique-guid")
                .iban("ES1234567890123456789012")
                .saldo("1500.75")
                .tipoCuentaId("1")
                .tarjetaId("tarjeta-12345")
                .clienteId("cliente-67890")
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-27T10:15:30.456")
                .isDeleted(false)
                .build();

        Page<CuentaResponse> page = new PageImpl<>(List.of(cuentaResponse));

        when(cuentaService.getAll(any(), any(), any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");


        mockMvc.perform(get("/v1/cuentas")
                        .param("iban", "ES1234567890123456789012")
                        .param("saldoMin", "1000")
                        .param("saldoMax", "2000")
                        .param("tipoCuenta", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guid").value("unique-guid"))
                .andExpect(jsonPath("$.content[0].iban").value("ES1234567890123456789012"))
                .andExpect(jsonPath("$.content[0].saldo").value(1500.75))
                .andExpect(jsonPath("$.content[0].tipoCuentaId").value("1"))
                .andExpect(jsonPath("$.content[0].tarjetaId").value("tarjeta-12345"))
                .andExpect(jsonPath("$.content[0].clienteId").value("cliente-67890"))
                .andExpect(jsonPath("$.content[0].createdAt").value("2024-11-26T15:23:45.123"))
                .andExpect(jsonPath("$.content[0].updatedAt").value("2024-11-27T10:15:30.456"))
                .andExpect(jsonPath("$.content[0].isDeleted").value(false));
    }

    @Test
    void GetById() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("unique-guid")
                .iban("ES1234567890123456789012")
                .saldo("1500.75")
                .tipoCuentaId("1")
                .tarjetaId("tarjeta-12345")
                .clienteId("cliente-67890")
                .build();


        when(cuentaService.getById("unique-guid")).thenReturn(cuentaResponse);

        mockMvc.perform(get("/v1/cuentas/unique-guid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("ES1234567890123456789012"))
                .andExpect(jsonPath("$.saldo").value("1500.75"))
                .andExpect(jsonPath("$.tipoCuentaId").value("1"))
                .andExpect(jsonPath("$.tarjetaId").value("tarjeta-12345"))
                .andExpect(jsonPath("$.clienteId").value("cliente-67890"));
    }


    @Test
    void Save() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("unique-guid")
                .iban("ES1234567890123456789012")
                .saldo("1500.75")
                .tipoCuentaId("1")
                .tarjetaId("tarjeta-12345")
                .clienteId("cliente-67890")
                .build();

        when(cuentaService.save(any(CuentaRequest.class))).thenReturn(cuentaResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/cuentas")
                        .contentType("application/json")
                        .content("{ \"iban\": \"ES1234567890123456789012\", \"saldo\": \"1500.75\", \"tipoCuentaId\": \"1\", \"tarjetaId\": \"tarjeta-12345\", \"clienteId\": \"cliente-67890\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.iban").value("ES1234567890123456789012"))
                .andExpect(jsonPath("$.saldo").value("1500.75"))
                .andExpect(jsonPath("$.tipoCuentaId").value("1"))
                .andExpect(jsonPath("$.tarjetaId").value("tarjeta-12345"))
                .andExpect(jsonPath("$.clienteId").value("cliente-67890"));
    }

    @Test
    void Update() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(BigDecimal.valueOf(1500.75))
                .tipoCuentaId("1")
                .tarjetaId("tarjeta-12345")
                .clienteId("cliente-67890")
                .build();

        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("unique-guid")
                .iban("ES1234567890123456789012")
                .saldo("1500.75")
                .tipoCuentaId("1")
                .tarjetaId("tarjeta-12345")
                .clienteId("cliente-67890")
                .build();


        when(cuentaService.update(eq("unique-guid"), any(CuentaRequestUpdate.class))).thenReturn(cuentaResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/cuentas/unique-guid")
                        .contentType("application/json")
                        .content("{ \"iban\": \"ES1234567890123456789012\", \"saldo\": \"1500.75\", \"tipoCuentaId\": \"1\", \"tarjetaId\": \"tarjeta-12345\", \"clienteId\": \"cliente-67890\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("ES1234567890123456789012"))
                .andExpect(jsonPath("$.saldo").value("1500.75"))
                .andExpect(jsonPath("$.tipoCuentaId").value("1"))
                .andExpect(jsonPath("$.tarjetaId").value("tarjeta-12345"))
                .andExpect(jsonPath("$.clienteId").value("cliente-67890"));
    }

    @Test
    void Delete() throws Exception {
        Mockito.doNothing().when(cuentaService).deleteById("unique-guid");

        mockMvc.perform(patch("/v1/cuentas/unique-guid"))
                .andExpect(status().isNoContent());
    }
    @Test
    void handleValidationExceptionUpdateError() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\", \"apellidos\": \"\", \"email\": \"\", \"telefono\": \"\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"))
                .andExpect(jsonPath("$.apellidos").value("Los apellidos no pueden estar vacio"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"email\":\"El email no puede estar vacio\"")
                        || responseContent.contains("\"email\":\"El email debe ser valido\"")),
                () -> assertTrue(responseContent.contains("\"telefono\":\"El telefono no puede estar vacio\"")
                        || responseContent.contains("\"telefono\":\"El telefono debe tener 9 numeros\""))
        );
    }
}