package org.example.vivesbankproject.tarjeta.service;

import org.example.vivesbankproject.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaUserPasswordNotValid;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarjetaServiceImplTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private UserRepository userRepository;

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

        tarjetaRequestPrivado = new TarjetaRequestPrivado();
        tarjetaRequestPrivado.setUsername("username");
        tarjetaRequestPrivado.setUserPass("password");
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
        assertEquals(123, result.getCvv());

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
    void save() {
        TarjetaRequestSave requestSave = TarjetaRequestSave.builder()
                .pin("123")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .build();

        when(tarjetaMapper.toTarjeta(requestSave)).thenReturn(tarjeta);
        when(tarjetaRepository.save(tarjeta)).thenReturn(tarjeta);
        when(tarjetaMapper.toTarjetaResponse(tarjeta)).thenReturn(tarjetaResponse);

        TarjetaResponse result = tarjetaService.save(requestSave);

        assertNotNull(result);
        assertEquals(GUID, result.getGuid());
        verify(tarjetaRepository).save(tarjeta);
    }

    @Test
    void update() {
        TarjetaRequestUpdate requestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000.00"))
                .limiteSemanal(new BigDecimal("10000.00"))
                .limiteMensual(new BigDecimal("40000.00"))
                .isDeleted(false)
                .build();

        Tarjeta tarjetaActualizada = tarjeta.builder()
                .limiteDiario(new BigDecimal("2000.00"))
                .limiteSemanal(new BigDecimal("10000.00"))
                .limiteMensual(new BigDecimal("40000.00"))
                .build();

        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(tarjeta));
        when(tarjetaMapper.toTarjetaUpdate(requestUpdate, tarjeta)).thenReturn(tarjetaActualizada);
        when(tarjetaRepository.save(tarjetaActualizada)).thenReturn(tarjetaActualizada);
        when(tarjetaMapper.toTarjetaResponse(tarjetaActualizada)).thenReturn(tarjetaResponse);

        TarjetaResponse result = tarjetaService.update(GUID, requestUpdate);

        assertNotNull(result);
        assertEquals(GUID, result.getGuid());
        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaRepository).save(tarjetaActualizada);
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
    void deleteById() {
        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.of(tarjeta));

        tarjetaService.deleteById(GUID);

        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaRepository).save(any(Tarjeta.class));
    }

    @Test
    void deleteByIdNotFound() {
        when(tarjetaRepository.findByGuid(GUID)).thenReturn(Optional.empty());

        assertThrows(TarjetaNotFound.class, () -> tarjetaService.deleteById(GUID));
        verify(tarjetaRepository).findByGuid(GUID);
        verify(tarjetaRepository, never()).save(any(Tarjeta.class));
    }
}