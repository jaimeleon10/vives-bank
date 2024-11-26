package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponsePrivado;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaMapperTest {

    private TarjetaMapper tarjetaMapper;
    private Tarjeta tarjetaMock;
    private final LocalDateTime NOW = LocalDateTime.now();
    private final LocalDate CADUCIDAD = LocalDate.now().plusYears(10);

    @BeforeEach
    void setUp() {
        tarjetaMapper = new TarjetaMapper();
        tarjetaMock = Tarjeta.builder()
                .id(1L)
                .guid("test-guid")
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
    }

    @Test
    void toTarjetaResponse() {
        TarjetaResponse response = tarjetaMapper.toTarjetaResponse(tarjetaMock);

        assertNotNull(response);
        assertEquals(tarjetaMock.getGuid(), response.getGuid());
        assertEquals(tarjetaMock.getNumeroTarjeta(), response.getNumeroTarjeta());
        assertEquals(tarjetaMock.getFechaCaducidad().toString(), response.getFechaCaducidad());
        assertEquals(tarjetaMock.getLimiteDiario().toString(), response.getLimiteDiario());
        assertEquals(tarjetaMock.getLimiteSemanal().toString(), response.getLimiteSemanal());
        assertEquals(tarjetaMock.getLimiteMensual().toString(), response.getLimiteMensual());
        assertEquals(tarjetaMock.getTipoTarjeta(), response.getTipoTarjeta());
        assertEquals(tarjetaMock.getCreatedAt().toString(), response.getCreatedAt());
        assertEquals(tarjetaMock.getUpdatedAt().toString(), response.getUpdatedAt());
        assertEquals(tarjetaMock.getIsDeleted(), response.getIsDeleted());
    }

    @Test
    void toTarjeta() {
        TarjetaRequestSave requestSave = TarjetaRequestSave.builder()
                .pin("123")
                .limiteDiario(new BigDecimal("1000.00"))
                .limiteSemanal(new BigDecimal("5000.00"))
                .limiteMensual(new BigDecimal("20000.00"))
                .tipoTarjeta(TipoTarjeta.DEBITO)
                .build();

        Tarjeta result = tarjetaMapper.toTarjeta(requestSave);

        assertNotNull(result);
        assertEquals(requestSave.getPin(), result.getPin());
        assertEquals(requestSave.getLimiteDiario(), result.getLimiteDiario());
        assertEquals(requestSave.getLimiteSemanal(), result.getLimiteSemanal());
        assertEquals(requestSave.getLimiteMensual(), result.getLimiteMensual());
        assertEquals(requestSave.getTipoTarjeta(), result.getTipoTarjeta());
    }

    @Test
    void toTarjetaUpdate() {
        TarjetaRequestUpdate requestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000.00"))
                .limiteSemanal(new BigDecimal("10000.00"))
                .limiteMensual(new BigDecimal("40000.00"))
                .isDeleted(true)
                .build();

        Tarjeta result = tarjetaMapper.toTarjetaUpdate(requestUpdate, tarjetaMock);

        assertNotNull(result);
        assertEquals(tarjetaMock.getId(), result.getId());
        assertEquals(tarjetaMock.getGuid(), result.getGuid());
        assertEquals(tarjetaMock.getNumeroTarjeta(), result.getNumeroTarjeta());
        assertEquals(tarjetaMock.getFechaCaducidad(), result.getFechaCaducidad());
        assertEquals(tarjetaMock.getCvv(), result.getCvv());
        assertEquals(tarjetaMock.getPin(), result.getPin());
        assertEquals(requestUpdate.getLimiteDiario(), result.getLimiteDiario());
        assertEquals(requestUpdate.getLimiteSemanal(), result.getLimiteSemanal());
        assertEquals(requestUpdate.getLimiteMensual(), result.getLimiteMensual());
        assertEquals(tarjetaMock.getTipoTarjeta(), result.getTipoTarjeta());
        assertEquals(tarjetaMock.getCreatedAt(), result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertEquals(requestUpdate.getIsDeleted(), result.getIsDeleted());
    }

    @Test
    void toTarjetaResponseCVV() {
        TarjetaResponsePrivado response = tarjetaMapper.toTarjetaPrivado(tarjetaMock);

        assertNotNull(response);
        assertEquals(tarjetaMock.getGuid(), response.getGuid());
        assertEquals(tarjetaMock.getPin(), response.getPin());
        assertEquals(tarjetaMock.getCvv().toString(), response.getCvv());
    }

    @Test
    void toTarjetaResponseException() {
        assertThrows(NullPointerException.class, () -> tarjetaMapper.toTarjetaResponse(null));
    }

    @Test
    void toTarjetaException() {
        assertThrows(NullPointerException.class, () -> tarjetaMapper.toTarjeta(null));
    }

    @Test
    void toTarjetaUpdatevonNulos() {
        assertThrows(NullPointerException.class,
                () -> tarjetaMapper.toTarjetaUpdate(null, tarjetaMock));
    }

    @Test
    void toTarjetaUpdateConNullTarjeta() {
        TarjetaRequestUpdate requestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000.00"))
                .isDeleted(true)
                .build();

        assertThrows(NullPointerException.class,
                () -> tarjetaMapper.toTarjetaUpdate(requestUpdate, null));
    }

    @Test
    void toTarjetaResponseCVVconNullos() {
        assertThrows(NullPointerException.class, () -> tarjetaMapper.toTarjetaPrivado(null));
    }


}