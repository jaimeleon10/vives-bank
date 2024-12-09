package org.example.vivesbankproject.cuenta.mappers;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TipoCuentaMapperTest {

    private TipoCuentaMapper mapper;
    private TipoCuenta tipoCuenta;
    private TipoCuentaRequest tipoCuentaRequest;

    @BeforeEach
    void setUp() {
        mapper = new TipoCuentaMapper();

        tipoCuenta = TipoCuenta.builder()
                .id(1L)
                .guid("guid-tipo-cuenta")
                .nombre("Ahorro")
                .interes(BigDecimal.valueOf(2.5))
                .createdAt(LocalDateTime.of(2022, 1, 1, 12, 0))
                .updatedAt(LocalDateTime.of(2022, 6, 1, 12, 0))
                .isDeleted(false)
                .build();

        tipoCuentaRequest = TipoCuentaRequest.builder()
                .nombre("Corriente")
                .interes(BigDecimal.valueOf(1.5))
                .build();
    }

    @Test
    void toTipoCuentaResponse() {
        var response = mapper.toTipoCuentaResponse(tipoCuenta);

        assertAll(
                () -> assertEquals(tipoCuenta.getGuid(), response.getGuid()),
                () -> assertEquals(tipoCuenta.getNombre(), response.getNombre()),
                () -> assertEquals(tipoCuenta.getInteres().toString(), response.getInteres()),
                () -> assertEquals(tipoCuenta.getCreatedAt().toString(), response.getCreatedAt()),
                () -> assertEquals(tipoCuenta.getUpdatedAt().toString(), response.getUpdatedAt()),
                () -> assertEquals(tipoCuenta.getIsDeleted(), response.getIsDeleted())
        );
    }

    @Test
    void toTipoCuenta() {
        var nuevoTipoCuenta = mapper.toTipoCuenta(tipoCuentaRequest);

        assertAll(
                () -> assertNull(nuevoTipoCuenta.getId(), "El ID debe ser nulo"),
                () -> assertNotNull(nuevoTipoCuenta.getGuid(), "El GUID debe ser generado automáticamente"),
                () -> assertEquals(tipoCuentaRequest.getNombre(), nuevoTipoCuenta.getNombre()),
                () -> assertEquals(tipoCuentaRequest.getInteres(), nuevoTipoCuenta.getInteres()),
                () -> assertNotNull(nuevoTipoCuenta.getCreatedAt(), "La fecha de creación debe ser generada automáticamente"),
                () -> assertNotNull(nuevoTipoCuenta.getUpdatedAt(), "La fecha de actualización debe ser generada automáticamente"),
                () -> assertNotNull(nuevoTipoCuenta.getIsDeleted(), "El campo isDeleted debe ser inicializado")
        );
    }

    @Test
    void toTipoCuentaUpdate() {
        var updatedTipoCuenta = mapper.toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta);

        assertAll(
                () -> assertEquals(tipoCuenta.getId(), updatedTipoCuenta.getId(), "El ID debe mantenerse igual"),
                () -> assertEquals(tipoCuenta.getGuid(), updatedTipoCuenta.getGuid(), "El GUID debe mantenerse igual"),
                () -> assertEquals(tipoCuentaRequest.getNombre(), updatedTipoCuenta.getNombre(), "El nombre debe actualizarse"),
                () -> assertEquals(tipoCuentaRequest.getInteres(), updatedTipoCuenta.getInteres(), "El interés debe actualizarse"),
                () -> assertEquals(tipoCuenta.getCreatedAt(), updatedTipoCuenta.getCreatedAt(), "La fecha de creación debe mantenerse igual"),
                () -> assertNotNull(updatedTipoCuenta.getUpdatedAt(), "La fecha de actualización debe generarse"),
                () -> assertTrue(updatedTipoCuenta.getUpdatedAt().isAfter(tipoCuenta.getUpdatedAt()), "La fecha de actualización debe ser más reciente"),
                () -> assertEquals(tipoCuenta.getIsDeleted(), updatedTipoCuenta.getIsDeleted(), "El estado de isDeleted debe mantenerse igual")
        );
    }
}
