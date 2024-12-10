package org.example.vivesbankproject.websocket.notifications.mappers;

import org.example.vivesbankproject.rest.movimientos.models.*;
import org.example.vivesbankproject.websocket.notifications.dto.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    private final NotificationMapper mapper = new NotificationMapper();

    private Domiciliacion domiciliacion;
    private Transferencia transferencia;
    private IngresoDeNomina ingreso;
    private PagoConTarjeta pago;

    @Test
    void testToIngresoNominaDto() {
        IngresoDeNomina ingreso = new IngresoDeNomina(
                "ES9121000418450200051336206",
                "ES6621000418450200051336206",
                500.00,
                "Empresa S.L.",
                "B12345678"
        );

        IngresoNominaResponse dto = mapper.toIngresoNominaDto(ingreso);

        assertNotNull(dto);
        assertEquals(ingreso.getIban_Origen(), dto.ibanOrigen());
        assertEquals(ingreso.getIban_Destino(), dto.ibanDestino());
        assertEquals(ingreso.getCantidad(), dto.cantidad());
        assertEquals(ingreso.getNombreEmpresa(), dto.nombreEmpresa());
        assertEquals(ingreso.getCifEmpresa(), dto.cifEmpresa());
    }

    @Test
    void testToDomiciliacionDto() {
        domiciliacion = new Domiciliacion().builder()
                .guid("guid-1234")
                .ibanOrigen("ES9121000418450200051336206")
                .ibanDestino("ES6621000418450200051336206")
                .cantidad(BigDecimal.valueOf(50.00))
                .nombreAcreedor("Cliente ABC")
                .fechaInicio(LocalDate.of(2024, 1, 1).atStartOfDay())
                .periodicidad(Periodicidad.MENSUAL)
                .ultimaEjecucion(LocalDate.of(2024, 11, 15).atStartOfDay())
                .activa(true)
                .build();

        DomiciliacionResponse dto = mapper.toDomiciliacionDto(domiciliacion);

        assertNotNull(dto);
        assertEquals(domiciliacion.getGuid(), dto.guid());
        assertEquals(domiciliacion.getIbanOrigen(), dto.ibanOrigen());
        assertEquals(domiciliacion.getIbanDestino(), dto.ibanDestino());
        assertEquals(domiciliacion.getCantidad(), dto.cantidad());
        assertEquals(domiciliacion.getNombreAcreedor(), dto.nombreAcreedor());
        assertEquals(domiciliacion.getFechaInicio().toString(), dto.fechaInicio());
        assertEquals(domiciliacion.getPeriodicidad().toString(), dto.periodicidad());
        assertEquals(domiciliacion.getActiva(), dto.activa());
        assertEquals(domiciliacion.getUltimaEjecucion().toString(), dto.ultimaEjecucion());
    }

    @Test
    void testToTransferenciaDto() {
        transferencia = new Transferencia().builder()
                .cantidad(BigDecimal.valueOf(50.00))
                .iban_Origen("ES9121000418450200051336206")
                .iban_Destino("ES6621000418450200051336206")
                .nombreBeneficiario("Cliente XYZ")
                .build();

        TransferenciaResponse dto = mapper.toTransferenciaDto(transferencia);

        assertNotNull(dto);
        assertEquals(transferencia.getIban_Origen(), dto.ibanOrigen());
        assertEquals(transferencia.getIban_Destino(), dto.ibanDestino());
        assertEquals(transferencia.getCantidad(), dto.cantidad());
        assertEquals(transferencia.getNombreBeneficiario(), dto.nombreBeneficiario());
    }

    @Test
    void testToPagoConTarjetaDto() {
        PagoConTarjeta pago = new PagoConTarjeta(
                "1234567812345678",
                75.50,
                "Tienda Online"
        );

        PagoConTarjetaResponse dto = mapper.toPagoConTarjetaDto(pago);

        assertNotNull(dto);
        assertEquals(pago.getNumeroTarjeta(), dto.numeroTarjeta());
        assertEquals(pago.getCantidad(), dto.cantidad());
        assertEquals(pago.getNombreComercio(), dto.nombreComercio());
    }
}
