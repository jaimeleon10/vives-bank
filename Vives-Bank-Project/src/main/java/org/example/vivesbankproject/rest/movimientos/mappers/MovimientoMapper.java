package org.example.vivesbankproject.rest.movimientos.mappers;

import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovimientoMapper {

    public Movimiento toMovimiento(MovimientoRequest movimientoRequest) {
        return Movimiento.builder()
                .guid(movimientoRequest.getGuid())
                .clienteGuid(movimientoRequest.getClienteGuid())
                .domiciliacion(movimientoRequest.getDomiciliacion())
                .ingresoDeNomina(movimientoRequest.getIngresoDeNomina())
                .pagoConTarjeta(movimientoRequest.getPagoConTarjeta())
                .transferencia(movimientoRequest.getTransferencia())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public MovimientoResponse toMovimientoResponse(Movimiento movimiento) {
        return MovimientoResponse.builder()
               .guid(movimiento.getGuid())
               .clienteGuid(movimiento.getClienteGuid())
               .domiciliacion(movimiento.getDomiciliacion())
               .ingresoDeNomina(movimiento.getIngresoDeNomina())
               .pagoConTarjeta(movimiento.getPagoConTarjeta())
               .transferencia(movimiento.getTransferencia())
               .isDeleted(movimiento.getIsDeleted())
                .createdAt(String.valueOf(movimiento.getCreatedAt()))
               .build();
    }

}
