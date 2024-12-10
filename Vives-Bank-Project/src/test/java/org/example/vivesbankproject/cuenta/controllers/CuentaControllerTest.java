package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    void save_InvalidTipoCuentaId() throws Exception {
        CuentaRequest cuentaRequest = CuentaRequest.builder()
                .tipoCuentaId("")
                .tarjetaId("tarjeta-123")
                .clienteId("cliente-456")
                .build();

        mockMvc.perform(post("/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipoCuentaId").value("El campo tipo de cuenta no puede estar vacío"));
    }

    @Test
    void save_InvalidTarjetaId() throws Exception {
        CuentaRequest cuentaRequest = CuentaRequest.builder()
                .tipoCuentaId("tipo-123")
                .tarjetaId("")
                .clienteId("cliente-456")
                .build();

        mockMvc.perform(post("/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tarjetaId").value("El campo tarjeta no puede estar vacío"));
    }

    @Test
    void save_InvalidClienteId() throws Exception {
        CuentaRequest cuentaRequest = CuentaRequest.builder()
                .tipoCuentaId("tipo-123")
                .tarjetaId("tarjeta-456")
                .clienteId("")
                .build();

        mockMvc.perform(post("/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clienteId").value("El campo cliente no puede estar vacío"));
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
    void update_InvalidSaldo() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(new BigDecimal("-10.00"))
                .tipoCuentaId("tipo-123")
                .tarjetaId("tarjeta-456")
                .clienteId("cliente-789")
                .isDeleted(false)
                .build();

        mockMvc.perform(put("/v1/cuentas/unique-guid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.saldo").value("El saldo no puede ser negativo"));
    }

    @Test
    void update_InvalidTipoCuentaId() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(new BigDecimal("100.00"))
                .tipoCuentaId(null)
                .tarjetaId("tarjeta-456")
                .clienteId("cliente-789")
                .isDeleted(false)
                .build();

        mockMvc.perform(put("/v1/cuentas/unique-guid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipoCuentaId").value("El campo del tipo de cuenta debe contener un id de tipo de cuenta"));
    }

    @Test
    void update_InvalidTarjetaId() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(new BigDecimal("100.00"))
                .tipoCuentaId("tipo-123")
                .tarjetaId(null) // No se permite nulo
                .clienteId("cliente-789")
                .isDeleted(false)
                .build();

        mockMvc.perform(put("/v1/cuentas/unique-guid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tarjetaId").value("El campo tarjeta debe contener un id de tarjeta"));
    }

    @Test
    void update_InvalidClienteId() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(new BigDecimal("100.00"))
                .tipoCuentaId("tipo-123")
                .tarjetaId("tarjeta-456")
                .clienteId(null)
                .isDeleted(false)
                .build();

        mockMvc.perform(put("/v1/cuentas/unique-guid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clienteId").value("El campo cliente debe contener un id de cliente"));
    }

    @Test
    void update_InvalidIsDeleted() throws Exception {
        CuentaRequestUpdate cuentaRequestUpdate = CuentaRequestUpdate.builder()
                .saldo(new BigDecimal("100.00"))
                .tipoCuentaId("tipo-123")
                .tarjetaId("tarjeta-456")
                .clienteId("cliente-789")
                .isDeleted(null)
                .build();

        mockMvc.perform(put("/v1/cuentas/unique-guid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isDeleted").value("El campo de borrado lógico no puede ser nulo"));
    }

    @Test
    void Delete() throws Exception {
        Mockito.doNothing().when(cuentaService).deleteById("unique-guid");

        mockMvc.perform(patch("/v1/cuentas/unique-guid"))
                .andExpect(status().isNoContent());
    }
    @Test
    void handleValidationExceptionUpdateError() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/cuentas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"saldo\": \"-1500.75\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.saldo").value("El saldo no puede ser negativo"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"saldo\":\"El saldo no puede ser negativo\""))
        );
    }

    @Test
    void getAll_sinParametrosDevuelvePaginaVacia() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("default-guid")
                .iban("ES0000000000000000000000")
                .saldo("1000.00")
                .build();

        PageImpl<CuentaResponse> page = new PageImpl<>(Collections.singletonList(cuentaResponse));

        when(cuentaService.getAll(
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                eq(Optional.empty()),
                any(PageRequest.class)
        )).thenReturn(page);

        when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");

        mockMvc.perform(get("/v1/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guid").value("default-guid"))
                .andExpect(jsonPath("$.content[0].iban").value("ES0000000000000000000000"));
    }

    @Test
    void getAllCuentasByClienteGuid() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("client-account")
                .iban("ES1111111111111111111111")
                .clienteId("existing-client")
                .build();

        when(cuentaService.getAllCuentasByClienteGuid("existing-client"))
                .thenReturn(new ArrayList<>(Collections.singletonList(cuentaResponse)));

        mockMvc.perform(get("/v1/cuentas/cliente/existing-client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].guid").value("client-account"))
                .andExpect(jsonPath("$[0].clienteId").value("existing-client"));
    }

    @Test
    void getByIban() throws Exception {
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("iban-account")
                .iban("ES2222222222222222222222")
                .build();

        when(cuentaService.getByIban("ES2222222222222222222222"))
                .thenReturn(cuentaResponse);

        mockMvc.perform(get("/v1/cuentas/iban/ES2222222222222222222222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value("iban-account"))
                .andExpect(jsonPath("$.iban").value("ES2222222222222222222222"));
    }

    @Test
    void delete_cuentaNoExistente() throws Exception {
        doNothing().when(cuentaService).deleteById("non-existent-guid");

        mockMvc.perform(patch("/v1/cuentas/non-existent-guid"))
                .andExpect(status().isNoContent());
    }
}