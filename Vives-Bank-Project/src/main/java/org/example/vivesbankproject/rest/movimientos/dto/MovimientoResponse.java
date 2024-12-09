package org.example.vivesbankproject.rest.movimientos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.rest.movimientos.models.Transferencia;
import org.example.vivesbankproject.utils.generators.IdGenerator;


/**
 * Clase de respuesta para el movimiento que incluye detalles como domiciliación, ingreso de nómina, pagos, transferencias,
 * información de cliente y atributos adicionales relacionados con el estado de la operación.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimientoResponse {

    /**
     * Identificador único generado para el movimiento.
     */
    @Schema(description = "Identificador único generado para el movimiento.", example = "abc123456")
    @Builder.Default
    private String guid = IdGenerator.generarId();

    /**
     * Identificador del cliente asociado al movimiento.
     */
    @Schema(description = "Identificador del cliente asociado al movimiento.", example = "cliente-001")
    private String clienteGuid;

    /**
     * Información relacionada con la domiciliación de este movimiento.
     */
    @Schema(description = "Información relacionada con la domiciliación de este movimiento.", implementation = Domiciliacion.class)
    private Domiciliacion domiciliacion;

    /**
     * Información relacionada con el ingreso de nómina de este movimiento.
     */
    @Schema(description = "Información relacionada con el ingreso de nómina de este movimiento.", implementation = IngresoDeNomina.class)
    private IngresoDeNomina ingresoDeNomina;

    /**
     * Información relacionada con el pago con tarjeta de este movimiento.
     */
    @Schema(description = "Información relacionada con el pago con tarjeta de este movimiento.", implementation = PagoConTarjeta.class)
    private PagoConTarjeta pagoConTarjeta;

    /**
     * Información relacionada con la transferencia de este movimiento.
     */
    @Schema(description = "Información relacionada con la transferencia de este movimiento.", implementation = Transferencia.class)
    private Transferencia transferencia;

    /**
     * Indica si el movimiento ha sido marcado como eliminado.
     */
    @Schema(description = "Indica si el movimiento ha sido marcado como eliminado.", example = "false")
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * Fecha de creación del movimiento en formato ISO 8601.
     */
    @Schema(description = "Fecha de creación del movimiento en formato ISO 8601.", example = "2023-12-01T10:15:30")
    private String createdAt;
}