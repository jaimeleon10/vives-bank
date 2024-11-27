package org.example.vivesbankproject.tarjeta.controller;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarjetaRestControllerTest {

    @Mock
    private TarjetaService tarjetaService;

    @Mock
    private PaginationLinksUtils paginationLinksUtils;

    @InjectMocks
    private TarjetaRestController tarjetaRestController;

    private TarjetaResponse tarjetaResponse;
    private TarjetaRequestSave tarjetaRequestSave;
    private TarjetaRequestUpdate tarjetaRequestUpdate;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        tarjetaResponse = TarjetaResponse.builder()
                .guid("idTest")
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(String.valueOf(LocalDate.now().plusYears(5)))
                .limiteDiario("1000")
                .limiteSemanal("5000")
                .limiteMensual("20000")
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-26T15:23:45.123")
                .isDeleted(false)
                .build();

        tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("123")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        tarjetaRequestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000"))
                .limiteSemanal(new BigDecimal("10000"))
                .limiteMensual(new BigDecimal("40000"))
                .isDeleted(false)
                .build();
    }

    @Test
    void getAllDevuelvePageResponse() {
        List<TarjetaResponse> tarjetas = List.of(tarjetaResponse);
        Page<TarjetaResponse> page = new PageImpl<>(tarjetas);
        when(tarjetaService.getAll(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TarjetaResponse>> response = tarjetaRestController.getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                0, 10, "id", "asc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tarjetaService).getAll(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getTarjetaById() {
        when(tarjetaService.getById(anyString())).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.getById("idTest");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).getById("idTest");
    }

    @Test
    void saveTarjeta() {
        when(tarjetaService.save(any(TarjetaRequestSave.class))).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.save(tarjetaRequestSave);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).save(tarjetaRequestSave);
    }

    @Test
    void updateTarjeta() {
        when(tarjetaService.update(anyString(), any(TarjetaRequestUpdate.class))).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.update("idTest", tarjetaRequestUpdate);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).update("idTest", tarjetaRequestUpdate);
    }

    @Test
    void deleteTarjeta() {
        doNothing().when(tarjetaService).deleteById(anyString());

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.deleteTarjeta("idTest");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tarjetaService).deleteById("idTest");
    }

    @Test
    void handleValidationExceptionError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        Map<String, String> errors = tarjetaRestController.handleValidationExceptions(ex);

        assertNotNull(errors);
        assertInstanceOf(HashMap.class, errors);
        verify(ex).getBindingResult();
    }

    @Test
    void getAllTarjetasConfiltros() {
        List<TarjetaResponse> tarjetas = List.of(tarjetaResponse);
        Page<TarjetaResponse> page = new PageImpl<>(tarjetas);
        when(tarjetaService.getAll(
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(PageRequest.class)
        )).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TarjetaResponse>> response = tarjetaRestController.getAll(
                Optional.of("1234567890123456"),
                Optional.of(LocalDate.now()),
                Optional.of(TipoTarjeta.CREDITO),
                Optional.of(new BigDecimal("500")),
                Optional.of(new BigDecimal("1500")),
                Optional.of(new BigDecimal("2500")),
                Optional.of(new BigDecimal("7500")),
                Optional.of(new BigDecimal("15000")),
                Optional.of(new BigDecimal("25000")),
                0, 10, "id", "desc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tarjetaService).getAll(
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(PageRequest.class)
        );
    }
}