package org.example.vivesbankproject.rest.movimientos.mappers;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper para convertir entre objetos de solicitud (DTO) y entidades relacionadas con movimientos.
 * Convierte solicitudes de movimientos en objetos de dominio `Movimiento` y viceversa.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
public class MovimientoMapper {

    /**
     * Convierte un objeto de solicitud de movimiento en una entidad `Movimiento`.
     *
     * @param movimientoRequest El objeto de solicitud con datos para el nuevo movimiento.
     * @return La entidad `Movimiento` correspondiente al objeto de solicitud.
     */
    @Schema(description = "Convierte una solicitud de movimiento en una entidad de tipo Movimiento")
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

    /**
     * Convierte una entidad `Movimiento` en una respuesta de tipo `MovimientoResponse`.
     *
     * @param movimiento La entidad `Movimiento` para convertir en respuesta.
     * @return El objeto `MovimientoResponse` que contiene la información relevante para la respuesta.
     */
    @Schema(description = "Convierte una entidad Movimiento en un objeto de tipo MovimientoResponse")
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