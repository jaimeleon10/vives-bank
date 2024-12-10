package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.vivesbankproject.rest.cuenta.controllers.TipoCuentaController;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.rest.cuenta.services.TipoCuentaService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.math.BigDecimal;
import java.util.*;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
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
        objectMapper = new ObjectMapper();

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
    void getAllConParametros() {
        Page<TipoCuentaResponse> mockPage = mock(Page.class);
        when(tipoCuentaService.getAll(
                eq(Optional.of("TestNombre")),
                eq(Optional.of(BigDecimal.TEN)),
                eq(Optional.of(BigDecimal.ONE)),
                any()
        )).thenReturn(mockPage);

        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TipoCuentaResponse>> response = tipoCuentaController.getAll(
                Optional.of("TestNombre"),
                Optional.of(BigDecimal.TEN),
                Optional.of(BigDecimal.ONE),
                0, 10, "id", "desc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tipoCuentaService).getAll(
                eq(Optional.of("TestNombre")),
                eq(Optional.of(BigDecimal.TEN)),
                eq(Optional.of(BigDecimal.ONE)),
                any()
        );
    }

    @Test
    void getAllconOrdenDescendente() {
        Page<TipoCuentaResponse> mockPage = mock(Page.class);
        when(tipoCuentaService.getAll(any(), any(), any(), any())).thenReturn(mockPage);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TipoCuentaResponse>> response = tipoCuentaController.getAll(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0, 10, "id", "DESC", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tipoCuentaService).getAll(any(), any(), any(), any());
    }

    @Test
    void getByIdconIdInexistente() {
        when(tipoCuentaService.getById(anyString())).thenThrow(new RuntimeException("Account type not found"));

        assertThrows(RuntimeException.class, () -> {
            tipoCuentaController.getById("nonExistentId");
        });
    }
}