package org.example.vivesbankproject.rest.movimientos.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Representa un movimiento financiero en el sistema, el cual puede contener distintos tipos de operaciones como domiciliación,
 * ingresos de nómina, pagos con tarjeta, y transferencias. También contiene información relacionada con la fecha de creación
 * y el estado del movimiento.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Document("movimientos")
@TypeAlias("Movimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Clase para representar un movimiento financiero en el sistema")
public class Movimiento {

    /**
     * Identificador único del movimiento en la base de datos, generado automáticamente.
     */
    @Id
    @Builder.Default
    @Schema(description = "Identificador único para el movimiento en la base de datos", example = "507f1f77bcf86cd799439011")
    private ObjectId id = new ObjectId();

    /**
     * GUID único para identificar este movimiento.
     */
    @Builder.Default
    @Schema(description = "GUID único para identificar el movimiento", example = "e4f2d782-b34d-4dc2-b672-8c1a72a6bb42")
    private String guid = IdGenerator.generarId();

    /**
     * Identificador del cliente relacionado con el movimiento.
     */
    @Schema(description = "Identificador del cliente asociado a este movimiento", example = "cliente123")
    private String clienteGuid;

    /**
     * Detalles de la domiciliación si este movimiento está relacionado con una domiciliación.
     */
    @Schema(description = "Detalles de la domiciliación asociada al movimiento")
    private Domiciliacion domiciliacion;

    /**
     * Detalles del ingreso de nómina si este movimiento está relacionado con un ingreso de nómina.
     */
    @Schema(description = "Detalles del ingreso de nómina asociado al movimiento")
    private IngresoDeNomina ingresoDeNomina;

    /**
     * Detalles del pago con tarjeta si este movimiento está relacionado con un pago con tarjeta.
     */
    @Schema(description = "Detalles del pago con tarjeta asociado al movimiento")
    private PagoConTarjeta pagoConTarjeta;

    /**
     * Detalles de la transferencia si este movimiento está relacionado con una transferencia.
     */
    @Schema(description = "Detalles de la transferencia asociada al movimiento")
    private Transferencia transferencia;

    /**
     * Indica si el movimiento está marcado como eliminado en el sistema.
     */
    @Builder.Default
    @Schema(description = "Indica si el movimiento ha sido marcado como eliminado", example = "false")
    private Boolean isDeleted = false;

    /**
     * Fecha y hora en la que se creó el movimiento por primera vez.
     */
    @Builder.Default
    @Schema(description = "Fecha y hora de creación del movimiento", example = "2023-12-08T14:30:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Obtiene el identificador en formato string.
     * Este campo es serializado como "_id" en la representación JSON.
     *
     * @return Identificador en formato String
     */
    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }
}

