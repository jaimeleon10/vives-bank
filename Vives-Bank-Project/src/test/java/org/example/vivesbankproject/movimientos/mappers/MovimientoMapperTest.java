package org.example.vivesbankproject.movimientos.mappers;

import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.movimientos.models.Transferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MovimientoMapperTest {

    private MovimientoMapper movimientoMapper;

    @BeforeEach
    void setUp() {
        movimientoMapper = new MovimientoMapper();
    }

    @Test
    void toMovimiento_FullRequest_MapsCorrectly() {
        // Arrange
        MovimientoRequest request = MovimientoRequest.builder()
                .guid("test-guid")
                .clienteGuid("cliente-guid")
                .domiciliacion(mock(Domiciliacion.class))
                .ingresoDeNomina(mock(IngresoDeNomina.class))
                .pagoConTarjeta(mock(PagoConTarjeta.class))
                .transferencia(mock(Transferencia.class))
                .build();

        // Act
        Movimiento movimiento = movimientoMapper.toMovimiento(request);

        // Assert
        assertEquals(request.getGuid(), movimiento.getGuid());
        assertEquals(request.getClienteGuid(), movimiento.getClienteGuid());
        assertEquals(request.getDomiciliacion(), movimiento.getDomiciliacion());
        assertEquals(request.getIngresoDeNomina(), movimiento.getIngresoDeNomina());
        assertEquals(request.getPagoConTarjeta(), movimiento.getPagoConTarjeta());
        assertEquals(request.getTransferencia(), movimiento.getTransferencia());
        assertFalse(movimiento.getIsDeleted());
        assertNotNull(movimiento.getCreatedAt());
    }

    @Test
    void toMovimiento_MinimalRequest_MapsWithDefaults() {
        // Arrange
        MovimientoRequest request = new MovimientoRequest();

        // Act
        Movimiento movimiento = movimientoMapper.toMovimiento(request);

        // Assert
        assertNull(movimiento.getGuid());
        assertNull(movimiento.getClienteGuid());
        assertNull(movimiento.getDomiciliacion());
        assertNull(movimiento.getIngresoDeNomina());
        assertNull(movimiento.getPagoConTarjeta());
        assertNull(movimiento.getTransferencia());
        assertFalse(movimiento.getIsDeleted());
        assertNotNull(movimiento.getCreatedAt());
    }

    @Test
    void toMovimientoResponse_FullMovimiento_MapsCorrectly() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Movimiento movimiento = Movimiento.builder()
                .guid("test-guid")
                .clienteGuid("cliente-guid")
                .domiciliacion(mock(Domiciliacion.class))
                .ingresoDeNomina(mock(IngresoDeNomina.class))
                .pagoConTarjeta(mock(PagoConTarjeta.class))
                .transferencia(mock(Transferencia.class))
                .isDeleted(true)
                .createdAt(now)
                .build();

        // Act
        MovimientoResponse response = movimientoMapper.toMovimientoResponse(movimiento);

        // Assert
        assertEquals(movimiento.getGuid(), response.getGuid());
        assertEquals(movimiento.getClienteGuid(), response.getClienteGuid());
        assertEquals(movimiento.getDomiciliacion(), response.getDomiciliacion());
        assertEquals(movimiento.getIngresoDeNomina(), response.getIngresoDeNomina());
        assertEquals(movimiento.getPagoConTarjeta(), response.getPagoConTarjeta());
        assertEquals(movimiento.getTransferencia(), response.getTransferencia());
        assertEquals(String.valueOf(now), response.getCreatedAt());
    }

    @Test
    void toMovimientoResponse_MinimalMovimiento_MapsWithDefaults() {
        // Arrange
        Movimiento movimiento = new Movimiento();

        // Act
        MovimientoResponse response = movimientoMapper.toMovimientoResponse(movimiento);

        // Assert
        assertNull(response.getGuid());
        assertNull(response.getClienteGuid());
        assertNull(response.getDomiciliacion());
        assertNull(response.getIngresoDeNomina());
        assertNull(response.getPagoConTarjeta());
        assertNull(response.getTransferencia());
        assertEquals("null", response.getCreatedAt());
    }

    @Test
    void toMovimientoResponse_NullCreatedAt_HandledGracefully() {
        // Arrange
        Movimiento movimiento = Movimiento.builder()
                .guid("test-guid")
                .createdAt(null)
                .build();

        // Act
        MovimientoResponse response = movimientoMapper.toMovimientoResponse(movimiento);

        // Assert
        assertEquals("null", response.getCreatedAt());
    }
}