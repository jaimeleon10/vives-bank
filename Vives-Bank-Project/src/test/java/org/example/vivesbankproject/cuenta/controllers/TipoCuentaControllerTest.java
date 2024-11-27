package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.services.TipoCuentaService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoCuentaControllerTest {

    @Mock
    private TipoCuentaService tipoCuentaService;

    @Mock
    private PaginationLinksUtils paginationLinksUtils;

    @InjectMocks
    private TipoCuentaController tipoCuentaController;

    private TipoCuentaResponse tipoCuentaResponse;
    private TipoCuentaRequest tipoCuentaRequest;
    private MockHttpServletRequest request;

    @MockBean
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        tipoCuentaResponse = TipoCuentaResponse.builder()
                .nombre("Cuenta Ahorros")
                .guid("guidTest")
                .interes("2.5")
                .build();

        tipoCuentaRequest = TipoCuentaRequest.builder()
                .nombre("Cuenta Ahorros")
                .interes(new BigDecimal("2.5"))
                .build();
    }

    @Test
    void getAllPageableDevuelvePageResponse() {
        List<TipoCuentaResponse> tipoCuentas = List.of(tipoCuentaResponse);
        Page<TipoCuentaResponse> page = new PageImpl<>(tipoCuentas);
        when(tipoCuentaService.getAll(any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TipoCuentaResponse>> response = tipoCuentaController.getAllPageable(
                Optional.empty(), Optional.empty(), 0, 10, "id", "asc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tipoCuentaService).getAll(any(), any(), any());
    }

    @Test
    void getTipoCuentaById() {
        when(tipoCuentaService.getById(anyString())).thenReturn(tipoCuentaResponse);

        ResponseEntity<TipoCuentaResponse> response = tipoCuentaController.getById("guidTest");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tipoCuentaResponse, response.getBody());
        verify(tipoCuentaService).getById("guidTest");
    }

    @Test
    void saveTipoCuenta() {
        when(tipoCuentaService.save(any(TipoCuentaRequest.class))).thenReturn(tipoCuentaResponse);

        ResponseEntity<TipoCuentaResponse> response = tipoCuentaController.save(tipoCuentaRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tipoCuentaResponse, response.getBody());
        verify(tipoCuentaService).save(tipoCuentaRequest);
    }

    @Test
    void updateTipoCuenta() {
        when(tipoCuentaService.update(anyString(), any(TipoCuentaRequest.class))).thenReturn(tipoCuentaResponse);

        ResponseEntity<TipoCuentaResponse> response = tipoCuentaController.update("guidTest", tipoCuentaRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tipoCuentaResponse, response.getBody());
        verify(tipoCuentaService).update("guidTest", tipoCuentaRequest);
    }

    @Test
    void deleteTipoCuenta() {
        TipoCuentaResponse tipoCuentaResponse = TipoCuentaResponse.builder()
                .guid("guidTest")
                .nombre("Cuenta de prueba")
                .interes("5.0")
                .build();

        when(tipoCuentaService.deleteById(anyString())).thenReturn(tipoCuentaResponse);

        ResponseEntity<TipoCuentaResponse> response = tipoCuentaController.delete("guidTest");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tipoCuentaResponse, response.getBody());
        verify(tipoCuentaService).deleteById("guidTest");
    }
    @Test
    void handleValidationExceptionError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        Map<String, String> errors = tipoCuentaController.handleValidationExceptions(ex);

        Assertions.assertNotNull(errors);
        assertInstanceOf(HashMap.class, errors);
        verify(ex).getBindingResult();
    }

    @Test
    void getAllTipoCuentasConFiltros() {
        List<TipoCuentaResponse> tipoCuentas = List.of(tipoCuentaResponse);
        Page<TipoCuentaResponse> page = new PageImpl<>(tipoCuentas);
        when(tipoCuentaService.getAll(any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TipoCuentaResponse>> response = tipoCuentaController.getAllPageable(
                Optional.of("Cuenta Ahorros"),
                Optional.of(new BigDecimal("2.5")),
                0, 10, "id", "asc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tipoCuentaService).getAll(any(), any(), any());
    }

   /* @Test
    void InvalidNombreTipoCuenta() throws Exception {
        TipoCuentaRequest tipoCuentaRequest = TipoCuentaRequest.builder()
                .nombre("")
                .interes(new BigDecimal("2.5"))
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tipocuentas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tipoCuentaRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El nombre del tipo de cuenta no puede estar vacío"))  // Verifica el mensaje de error para 'nombre'
        );
    }

    @Test
    void InvalidInteresTipoCuenta() throws Exception {
        TipoCuentaRequest tipoCuentaRequest = TipoCuentaRequest.builder()
                .nombre("Cuenta Ahorro")
                .interes(new BigDecimal("-2.5"))
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tipocuentas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tipoCuentaRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El interés no puede ser negativo"))  // Verifica el mensaje de error para 'interes'
        );
    }

    @Test
    void InvalidInteresDecimalTipoCuenta() throws Exception {
        TipoCuentaRequest tipoCuentaRequest = TipoCuentaRequest.builder()
                .nombre("Cuenta Ahorro")
                .interes(new BigDecimal("2.555"))
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tipocuentas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tipoCuentaRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El interés debe ser un número válido"))  // Verifica el mensaje de error para el campo 'interes'
        );
    } */
}