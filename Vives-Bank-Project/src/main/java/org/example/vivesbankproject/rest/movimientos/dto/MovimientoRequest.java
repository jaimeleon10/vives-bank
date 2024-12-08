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
 * Clase que representa la solicitud para realizar un movimiento. Incluye varios tipos de movimiento relacionados con el
 * sistema de operaciones financieras, como domiciliación, ingreso de nómina, pago con tarjeta y transferencia.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimientoRequest {

    /**
     * Identificador único generado para el movimiento.
     */
    @Schema(description = "Identificador único generado para el movimiento.", example = "abc123456")
    @Builder.Default
    private String guid = IdGenerator.generarId();

    /**
     * Identificador del cliente relacionado con el movimiento.
     */
    @Schema(description = "Identificador del cliente relacionado con el movimiento.", example = "cliente-001")
    private String clienteGuid;

    /**
     * Información relacionada con el tipo de domiciliación.
     */
    @Schema(description = "Información relacionada con el tipo de domiciliación.", implementation = Domiciliacion.class)
    private Domiciliacion domiciliacion;

    /**
     * Información relacionada con el tipo de ingreso de nómina.
     */
    @Schema(description = "Información relacionada con el tipo de ingreso de nómina.", implementation = IngresoDeNomina.class)
    private IngresoDeNomina ingresoDeNomina;

    /**
     * Información relacionada con el tipo de pago con tarjeta.
     */
    @Schema(description = "Información relacionada con el tipo de pago con tarjeta.", implementation = PagoConTarjeta.class)
    private PagoConTarjeta pagoConTarjeta;

    /**
     * Información relacionada con el tipo de transferencia.
     */
    @Schema(description = "Información relacionada con el tipo de transferencia.", implementation = Transferencia.class)
    private Transferencia transferencia;
}

