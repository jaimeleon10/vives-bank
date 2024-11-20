package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarjetaMapperTest {

    @Mock
    private TarjetaService tarjetaService;

    private TarjetaMapper tarjetaMapper;

    @BeforeEach
    void setUp() {
        tarjetaMapper = new TarjetaMapper(tarjetaService);
    }

    @Test
    void testToTarjeta() {
        TipoTarjeta tipoTarjeta = TipoTarjeta.builder()
                .id(UUID.randomUUID())
                .nombre(Tipo.CREDITO)
                .build();
        TarjetaRequest request = TarjetaRequest.builder()
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .pin("1234")
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta(Tipo.CREDITO.name())
                .build();

        when(tarjetaService.getTipoTarjetaByNombre(Tipo.CREDITO)).thenReturn(tipoTarjeta);

        Tarjeta result = tarjetaMapper.toTarjeta(request);

        assertNotNull(result);
        assertEquals(request.getNumeroTarjeta(), result.getNumeroTarjeta());
        assertEquals(request.getFechaCaducidad(), result.getFechaCaducidad());
        assertEquals(request.getCvv(), result.getCvv());
        assertEquals(request.getPin(), result.getPin());
        assertEquals(request.getLimiteDiario(), result.getLimiteDiario());
        assertEquals(request.getLimiteSemanal(), result.getLimiteSemanal());
        assertEquals(request.getLimiteMensual(), result.getLimiteMensual());
        assertEquals(tipoTarjeta, result.getTipoTarjeta());
        assertNotNull(result.getId());
    }

    @Test
    void testToTarjetaResponse() {
        TipoTarjeta tipoTarjeta = TipoTarjeta.builder()
                .id(UUID.randomUUID())
                .nombre(Tipo.CREDITO)
                .build();
        Tarjeta tarjeta = Tarjeta.builder()
                .id(UUID.randomUUID())
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta(tipoTarjeta)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TarjetaResponse result = tarjetaMapper.toTarjetaResponse(tarjeta);

        assertNotNull(result);
        assertEquals(tarjeta.getId(), result.getId());
        assertEquals(tarjeta.getNumeroTarjeta(), result.getNumeroTarjeta());
        assertEquals(tarjeta.getFechaCaducidad(), result.getFechaCaducidad());
        assertEquals(tarjeta.getCvv(), result.getCvv());
        assertEquals(tarjeta.getLimiteDiario(), result.getLimiteDiario());
        assertEquals(tarjeta.getLimiteSemanal(), result.getLimiteSemanal());
        assertEquals(tarjeta.getLimiteMensual(), result.getLimiteMensual());
        assertEquals(tarjeta.getTipoTarjeta().getNombre().name(), result.getTipoTarjeta());
        assertEquals(tarjeta.getCreatedAt(), result.getCreatedAt());
        assertEquals(tarjeta.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void testToTarjetaResponseNullTarjeta() {
        TarjetaResponse result = tarjetaMapper.toTarjetaResponse(null);

        assertNull(result);
    }

    @Test
    void ToTarjetaResponseNullTipoTarjeta() {
        Tarjeta tarjeta = Tarjeta.builder()
                .id(UUID.randomUUID())
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TarjetaResponse result = tarjetaMapper.toTarjetaResponse(tarjeta);

        assertNotNull(result);
        assertNull(result.getTipoTarjeta());
    }

    @Test
    void testToRequest() {
        TipoTarjeta tipoTarjeta = TipoTarjeta.builder()
                .id(UUID.randomUUID())
                .nombre(Tipo.CREDITO)
                .build();
        Tarjeta tarjeta = Tarjeta.builder()
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .pin("1234")
                .limiteDiario(BigDecimal.valueOf(1000))
                .limiteSemanal(BigDecimal.valueOf(5000))
                .limiteMensual(BigDecimal.valueOf(20000))
                .tipoTarjeta(tipoTarjeta)
                .build();

        TarjetaRequest result = tarjetaMapper.toRequest(tarjeta);

        assertNotNull(result);
        assertEquals(tarjeta.getNumeroTarjeta(), result.getNumeroTarjeta());
        assertEquals(tarjeta.getFechaCaducidad(), result.getFechaCaducidad());
        assertEquals(tarjeta.getCvv(), result.getCvv());
        assertEquals(tarjeta.getPin(), result.getPin());
        assertEquals(tarjeta.getLimiteDiario(), result.getLimiteDiario());
        assertEquals(tarjeta.getLimiteSemanal(), result.getLimiteSemanal());
        assertEquals(tarjeta.getLimiteMensual(), result.getLimiteMensual());
        assertEquals(tarjeta.getTipoTarjeta().getNombre().name(), result.getTipoTarjeta());
    }
}