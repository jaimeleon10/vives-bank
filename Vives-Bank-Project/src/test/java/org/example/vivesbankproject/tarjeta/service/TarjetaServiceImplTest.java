package org.example.vivesbankproject.tarjeta.service;


import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaUserPasswordNotValid;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarjetaServiceImplTest {

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private TarjetaMapper tarjetaMapper;

    @InjectMocks
    private TarjetaServiceImpl tarjetaService;

    private Tarjeta tarjeta;
    private TarjetaResponse tarjetaResponse;
    private TarjetaResponsePrivado tarjetaResponsePrivado;
    private TarjetaRequestPrivado tarjetaRequestPrivado;
    private final String GUID = "test-guid";
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDate CADUCIDAD = LocalDate.now().plusYears(10);

    @BeforeEach
    void setUp() {

        WebSocketHandler mockWebSocketHandler = mock(WebSocketHandler.class);
        when(webSocketConfig.webSocketTarjetasHandler()).thenReturn(mockWebSocketHandler);

        tarjetaService = new TarjetaServiceImpl(
                tarjetaRepository,
                tarjetaMapper,
                userRepository,
                webSocketConfig,
                cuentaRepository
        );

        tarjeta = Tarjeta.builder()
                .id(1L)
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(CADUCIDAD)
                .cvv(123)
                .pin("123")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .createdAt(NOW)
                .updatedAt(NOW)
                .isDeleted(false)
                .build();

        tarjetaResponse = TarjetaResponse.builder()
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(String.valueOf(CADUCIDAD))
                .limiteDiario("1000.00")
                .limiteSemanal("5000.00")
                .limiteMensual("20000.00")
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .createdAt(String.valueOf(NOW))
                .updatedAt(String.valueOf(NOW))
                .isDeleted(false)
                .build();

        tarjetaResponsePrivado = TarjetaResponsePrivado.builder()
                .guid(GUID)
                .cvv("123")
                .build();

        tarjetaRequestPrivado = TarjetaRequestPrivado.builder()
                .username("")
                .userPass("password")
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tarjeta> tarjetaPage = new PageImpl<>(List.of(tarjeta));

        when(tarjetaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(tarjetaPage);
        when(tarjetaMapper.toTarjetaResponse(any(Tarjeta.class))).thenReturn(tarjetaResponse);

        Page<TarjetaResponse> result = tarjetaService.getAll(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(tarjetaResponse, result.getContent().get(0));
        verify(tarjetaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse result = tarjetaService.getById(GUID);

        assertNotNull(result);
        assertEquals(GUID, result.getGuid());
        verify(tarjetaRepository).findByGuid(GUID);
    }

    @Test
    void save() {
        TarjetaRequestSave requestSave = TarjetaRequestSave.builder()
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .limiteDiario(new BigDecimal("1500.00"))
                .limiteSemanal(new BigDecimal("7500.00"))
                .limiteMensual(new BigDecimal("25000.00"))
                .pin("1234")
                .build();

        Tarjeta savedTarjeta = Tarjeta.builder()
                .guid("new-guid")
                .numeroTarjeta(requestSave.getPin())
                .limiteDiario(requestSave.getLimiteDiario())
                .limiteSemanal(requestSave.getLimiteSemanal())
                .limiteMensual(requestSave.getLimiteMensual())
                .tipoTarjeta(requestSave.getTipoTarjeta())
                .build();

        TarjetaResponse expectedResponse = TarjetaResponse.builder()
                .guid("new-guid")
                .limiteDiario(requestSave.getLimiteDiario().toString())
                .limiteSemanal(requestSave.getLimiteSemanal().toString())
                .limiteMensual(requestSave.getLimiteMensual().toString())
                .tipoTarjeta(requestSave.getTipoTarjeta())
                .build();

        when(tarjetaMapper.toTarjeta(requestSave)).thenReturn(savedTarjeta);
        when(tarjetaRepository.save(savedTarjeta)).thenReturn(savedTarjeta);
        when(tarjetaMapper.toTarjetaResponse(savedTarjeta)).thenReturn(expectedResponse);

        TarjetaResponse result = tarjetaService.save(requestSave);

        assertNotNull(result);
        assertEquals(expectedResponse.getGuid(), result.getGuid());
        assertEquals(requestSave.getTipoTarjeta(), result.getTipoTarjeta());

        verify(tarjetaMapper).toTarjeta(requestSave);
        verify(tarjetaRepository).save(savedTarjeta);
        verify(tarjetaMapper).toTarjetaResponse(savedTarjeta);
    }

    @Test
    void update() {
        TarjetaRequestUpdate requestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2500.00"))
                .limiteSemanal(new BigDecimal("12500.00"))
                .limiteMensual(new BigDecimal("50000.00"))
                .isDeleted(false)
                .build();

        Tarjeta existingTarjeta = Tarjeta.builder()
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .build();

        Tarjeta updatedTarjeta = Tarjeta.builder()
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .limiteDiario(requestUpdate.getLimiteDiario())
                .limiteSemanal(requestUpdate.getLimiteSemanal())
                .limiteMensual(requestUpdate.getLimiteMensual())
                .build();

        TarjetaResponse expectedResponse = TarjetaResponse.builder()
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .limiteDiario(requestUpdate.getLimiteDiario().toString())
                .limiteSemanal(requestUpdate.getLimiteSemanal().toString())
                .limiteMensual(requestUpdate.getLimiteMensual().toString())
                .build();

        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(existingTarjeta));
        when(tarjetaMapper.toTarjetaUpdate(requestUpdate, existingTarjeta)).thenReturn(updatedTarjeta);
        when(tarjetaRepository.save(updatedTarjeta)).thenReturn(updatedTarjeta);
        when(tarjetaMapper.toTarjetaResponse(updatedTarjeta)).thenReturn(expectedResponse);

        TarjetaResponse result = tarjetaService.update(GUID, requestUpdate);

        assertNotNull(result);
        assertEquals(GUID, result.getGuid());
        assertEquals(requestUpdate.getLimiteDiario().toString(), result.getLimiteDiario());
        assertEquals(requestUpdate.getLimiteSemanal().toString(), result.getLimiteSemanal());
        assertEquals(requestUpdate.getLimiteMensual().toString(), result.getLimiteMensual());

        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaMapper).toTarjetaUpdate(requestUpdate, existingTarjeta);
        verify(tarjetaRepository).save(updatedTarjeta);
        verify(tarjetaMapper).toTarjetaResponse(updatedTarjeta);
    }

    @Test
    void delete() {
        Tarjeta existingTarjeta = Tarjeta.builder()
                .id(1L)
                .guid(GUID)
                .numeroTarjeta("1234567890123456")
                .isDeleted(false)
                .build();

        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(existingTarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(existingTarjeta);

        tarjetaService.deleteById(GUID);

        verify(tarjetaRepository).findByGuid(GUID);

        ArgumentCaptor<Tarjeta> tarjetaCaptor = ArgumentCaptor.forClass(Tarjeta.class);
        verify(tarjetaRepository).save(tarjetaCaptor.capture());

        Tarjeta savedTarjeta = tarjetaCaptor.getValue();
        assertEquals(GUID, savedTarjeta.getGuid());
    }

    @Test
    void getByIdNotFound() {
        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.getById(GUID));
        verify(tarjetaRepository).findByGuid(GUID);
    }

    @Test
    void getPrivado() {
        var user = new User();
        user.setUsername(tarjetaRequestPrivado.getUsername());
        user.setPassword(tarjetaRequestPrivado.getUserPass());
        when(userRepository.findByUsername(tarjetaRequestPrivado.getUsername())).thenReturn(Optional.of(user));

        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaPrivado(tarjeta)).thenReturn(tarjetaResponsePrivado);

        TarjetaResponsePrivado result = tarjetaService.getPrivateData(GUID, tarjetaRequestPrivado);

        assertNotNull(result);
        assertEquals(GUID, result.getGuid());
        assertEquals("123", result.getCvv());

        verify(userRepository).findByUsername(tarjetaRequestPrivado.getUsername());
        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaMapper).toTarjetaPrivado(tarjeta);
    }

    @Test
    void getPrivadoNotFound() {
        when(userRepository.findByUsername(tarjetaRequestPrivado.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByUsername.class, () -> tarjetaService.getPrivateData(GUID, tarjetaRequestPrivado));

        verify(userRepository).findByUsername(tarjetaRequestPrivado.getUsername());

        verifyNoInteractions(tarjetaRepository);
    }

    @Test
    void getPrivadoPasswordNotValid() {
        var user = new User();
        user.setPassword("wrongPassword");
        when(userRepository.findByUsername(tarjetaRequestPrivado.getUsername())).thenReturn(Optional.of(user));

        assertThrows(TarjetaUserPasswordNotValid.class, () -> tarjetaService.getPrivateData(GUID, tarjetaRequestPrivado));

        verify(userRepository).findByUsername(tarjetaRequestPrivado.getUsername());
        verifyNoInteractions(tarjetaRepository);
    }

    @Test
    void updateNotFound() {
        TarjetaRequestUpdate requestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000.00"))
                .limiteSemanal(new BigDecimal("10000.00"))
                .limiteMensual(new BigDecimal("40000.00"))
                .isDeleted(false)
                .build();

        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.update(GUID, requestUpdate));
        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaRepository, never()).save(any(Tarjeta.class));
    }

    @Test
    void deleteByIdNotFound() {
        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.deleteById(GUID));
        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaRepository, never()).save(any(Tarjeta.class));
    }

    @Test
    void getAll_WithoutMinLimiteSemanal_ShouldReturnAllCards() {
        Pageable pageable = PageRequest.of(0, 10);

        Tarjeta tarjeta1 = Tarjeta.builder()
                .guid("tarjeta1")
                .limiteSemanal(new BigDecimal("3000.00"))
                .build();

        Tarjeta tarjeta2 = Tarjeta.builder()
                .guid("tarjeta2")
                .limiteSemanal(new BigDecimal("6000.00"))
                .build();

        Page<Tarjeta> tarjetaPage = new PageImpl<>(List.of(tarjeta1, tarjeta2));

        when(tarjetaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(tarjetaPage);

        when(tarjetaMapper.toTarjetaResponse(any(Tarjeta.class)))
                .thenReturn(tarjetaResponse);

        Page<TarjetaResponse> result = tarjetaService.getAll(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                pageable
        );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(tarjetaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getByNumeroTarjeta() {
        when(tarjetaRepository.findByNumeroTarjeta("1234567890123456")).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse result = tarjetaService.getByNumeroTarjeta("1234567890123456");

        assertNotNull(result);
        assertEquals("1234567890123456", result.getNumeroTarjeta());
        verify(tarjetaRepository).findByNumeroTarjeta("1234567890123456");
        verify(tarjetaMapper).toTarjetaResponse(tarjeta);
    }


    @Test
    void getByNumeroTarjetaNotFound() {
        when(tarjetaRepository.findByNumeroTarjeta("1234567890123456")).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFoundByNumero.class, () -> tarjetaService.getByNumeroTarjeta("1234567890123456"));
        verify(tarjetaRepository).findByNumeroTarjeta("1234567890123456");

        verifyNoInteractions(tarjetaMapper);
    }
}