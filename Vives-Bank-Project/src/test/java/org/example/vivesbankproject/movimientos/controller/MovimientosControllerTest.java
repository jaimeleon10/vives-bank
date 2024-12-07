package org.example.vivesbankproject.movimientos.controller;

import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.services.MovimientosService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientosControllerTest {

    @Mock
    private MovimientosService service;

    @Mock
    private PaginationLinksUtils paginationLinksUtils;

    @InjectMocks
    private MovimientosController movimientosController;

    private MovimientoResponse movimientoResponse;
    private MovimientoRequest movimientoRequest;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        Transferencia transferencia = Transferencia.builder()
                .cantidad(BigDecimal.valueOf(100.00))
                .iban_Origen("ES1234567890")
                .iban_Destino("ES0987654321")
                .build();

        movimientoResponse = MovimientoResponse.builder()
                .guid("test-guid")
                .clienteGuid("cliente-test")
                .transferencia(transferencia)
                .createdAt(String.valueOf(LocalDateTime.now()))
                .build();

        movimientoRequest = MovimientoRequest.builder()
                .clienteGuid("cliente-test")
                .transferencia(transferencia)
                .build();

        mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/v1/movimientos");
        mockRequest.setServerName("localhost");
        mockRequest.setScheme("http");
        mockRequest.setServerPort(8080);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void getAllMovimientos_ShouldReturnPageOfMovimientos() {
        List<MovimientoResponse> movimientos = List.of(movimientoResponse);
        Page<MovimientoResponse> page = new PageImpl<>(movimientos);

        when(service.getAll(any(Pageable.class))).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("link-header");

        ResponseEntity<PageResponse<MovimientoResponse>> response = movimientosController.getAll(0, 10, "id", "asc", mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        verify(service).getAll(any(Pageable.class));
    }

    @Test
    void getByGuid_ShouldReturnMovimiento() {
        when(service.getByGuid("test-guid")).thenReturn(movimientoResponse);

        ResponseEntity<MovimientoResponse> response = movimientosController.getById("test-guid");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimientoResponse, response.getBody());
        verify(service).getByGuid("test-guid");
    }

    @Test
    void getByClienteGuid_ShouldReturnMovimiento() {
        when(service.getByClienteGuid("cliente-test")).thenReturn(movimientoResponse);

        ResponseEntity<MovimientoResponse> response = movimientosController.getByClienteGuid("cliente-test");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimientoResponse, response.getBody());
        verify(service).getByClienteGuid("cliente-test");
    }

    @Test
    void saveMovimiento_ShouldReturnSavedMovimiento() {
        when(service.save(any(MovimientoRequest.class))).thenReturn(movimientoResponse);

        ResponseEntity<MovimientoResponse> response = movimientosController.save(movimientoRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movimientoResponse, response.getBody());
        verify(service).save(movimientoRequest);
    }

    @Test
    void handleValidationExceptions_MethodArgumentNotValid() {
        // This test would typically involve creating a mock MethodArgumentNotValidException
        // and verifying the error handling mechanism
    }

    @Test
    void handleValidationExceptions_ConstraintViolation() {
        // Similar to the previous test, this would involve creating a mock
        // ConstraintViolationException and verifying the error handling
    }
}