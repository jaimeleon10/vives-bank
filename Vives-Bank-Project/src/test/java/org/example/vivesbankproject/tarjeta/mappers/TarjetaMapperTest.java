package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class TarjetaMapperTest {

    private TarjetaMapper tarjetaMapper;

    @BeforeEach
    void setUp() {
        tarjetaMapper = new TarjetaMapper();
    }

    @Test
    void toRequestATarjeta() {
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
                .tipoTarjeta(tipoTarjeta)
                .build();

        Tarjeta tarjeta = tarjetaMapper.toTarjeta(request);

        assertEquals(request.getNumeroTarjeta(), tarjeta.getNumeroTarjeta());
        assertEquals(request.getFechaCaducidad(), tarjeta.getFechaCaducidad());
        assertEquals(request.getCvv(), tarjeta.getCvv());
        assertEquals(request.getPin(), tarjeta.getPin());
        assertEquals(request.getLimiteDiario(), tarjeta.getLimiteDiario());
        assertEquals(request.getLimiteSemanal(), tarjeta.getLimiteSemanal());
        assertEquals(request.getLimiteMensual(), tarjeta.getLimiteMensual());
        assertEquals(request.getTipoTarjeta(), tarjeta.getTipoTarjeta());
    }

    @Test
    void toTarjetaAResponse() {
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
                .build();

        TarjetaResponse response = tarjetaMapper.toTarjetaResponse(tarjeta);

        assertEquals(tarjeta.getId(), response.getId());
        assertEquals(tarjeta.getNumeroTarjeta(), response.getNumeroTarjeta());
        assertEquals(tarjeta.getFechaCaducidad(), response.getFechaCaducidad());
        assertEquals(tarjeta.getCvv(), response.getCvv());
        assertEquals(tarjeta.getLimiteDiario(), response.getLimiteDiario());
        assertEquals(tarjeta.getLimiteSemanal(), response.getLimiteSemanal());
        assertEquals(tarjeta.getLimiteMensual(), response.getLimiteMensual());
        assertEquals(tarjeta.getTipoTarjeta(), response.getTipoTarjeta());
        assertEquals(tarjeta.getCreatedAt(), response.getCreatedAt());
        assertEquals(tarjeta.getUpdatedAt(), response.getUpdatedAt());
    }

    @Test
    void toTarjetaARequest() {

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

        TarjetaRequest request = tarjetaMapper.toRequest(tarjeta);

        assertEquals(tarjeta.getNumeroTarjeta(), request.getNumeroTarjeta());
        assertEquals(tarjeta.getFechaCaducidad(), request.getFechaCaducidad());
        assertEquals(tarjeta.getCvv(), request.getCvv());
        assertEquals(tarjeta.getPin(), request.getPin());
        assertEquals(tarjeta.getLimiteDiario(), request.getLimiteDiario());
        assertEquals(tarjeta.getLimiteSemanal(), request.getLimiteSemanal());
        assertEquals(tarjeta.getLimiteMensual(), request.getLimiteMensual());
        assertEquals(tarjeta.getTipoTarjeta(), request.getTipoTarjeta());
    }

    @Test
    void toRequesConNulos() {
        TarjetaRequest requestNulo = null;
        Tarjeta tarjetaNula = null;

        assertThrows(NullPointerException.class, () -> tarjetaMapper.toTarjeta(requestNulo));
        assertThrows(NullPointerException.class, () -> tarjetaMapper.toTarjetaResponse(tarjetaNula));
        assertThrows(NullPointerException.class, () -> tarjetaMapper.toRequest(tarjetaNula));
    }
}