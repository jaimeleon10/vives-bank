package org.example.vivesbankproject.rest.movimientos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una domiciliación en el sistema financiero. Esta clase mapea los detalles relacionados
 * con la domiciliación de pagos, configurando información como IBAN origen, destino, cantidad, frecuencia, entre otros.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "domiciliaciones")
@Schema(description = "Clase para representar una domiciliación en el sistema financiero")
public class Domiciliacion {

    /**
     * Identificador único de la domiciliación generado automáticamente.
     */
    @Id
    @Builder.Default
    @Schema(description = "Identificador único de la domiciliación", example = "507f1f77bcf86cd799439011")
    private ObjectId id = new ObjectId();

    /**
     * GUID único generado para la domiciliación.
     */
    @Builder.Default
    @Schema(description = "GUID único para la domiciliación", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid = IdGenerator.generarId();

    /**
     * Identificador del cliente asociado a esta domiciliación.
     */
    @Schema(description = "Identificador del cliente relacionado con la domiciliación", example = "cliente-001")
    private String clienteGuid;

    /**
     * IBAN de origen para la domiciliación.
     */
    @NotBlank
    @Schema(description = "IBAN de origen para realizar la domiciliación", example = "ES9121000000410200051332")
    private String ibanOrigen;

    /**
     * IBAN de destino para la domiciliación.
     */
    @NotBlank
    @Schema(description = "IBAN de destino para la domiciliación", example = "ES9121000000410200051333")
    private String ibanDestino;

    /**
     * Cantidad de dinero a domiciliar, con restricciones de rango válido.
     */
    @Min(value = 1, message = "El importe no puede ser menor que 1")
    @Max(value = 10000, message = "El importe no puede ser mayor que 1000000000000")
    @Schema(description = "Cantidad a domiciliar", example = "100.50")
    private BigDecimal cantidad;

    /**
     * Nombre del acreedor relacionado con la domiciliación. Máximo 100 caracteres.
     */
    @Size(max = 100, message = "El nombre del acreedor no puede superar los 100 caracteres")
    @Schema(description = "Nombre del acreedor relacionado con la domiciliación", example = "Acreedor Ejemplo")
    private String nombreAcreedor;

    /**
     * Fecha de inicio de la domiciliación.
     */
    @NotNull
    @Builder.Default
    @Schema(description = "Fecha de inicio de la domiciliación", example = "2024-12-01T00:00:00")
    private LocalDateTime fechaInicio = LocalDateTime.now();

    /**
     * Frecuencia con la que se ejecuta la domiciliación.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Periodicidad de la domiciliación", example = "MENSUAL")
    private Periodicidad periodicidad = Periodicidad.MENSUAL;

    /**
     * Indica si la domiciliación está activa o no.
     */
    @Builder.Default
    @Schema(description = "Indica si la domiciliación está activa", example = "true")
    private Boolean activa = true;

    /**
     * Fecha de la última ejecución de la domiciliación.
     */
    @Builder.Default
    @Schema(description = "Última fecha de ejecución de la domiciliación", example = "2024-12-08T10:00:00")
    private LocalDateTime ultimaEjecucion = LocalDateTime.now(); // Última vez que se realizó el cargo

    /**
     * Convierte el campo `id` de tipo ObjectId en una cadena hexadecimal para su uso en las respuestas JSON.
     *
     * @return El identificador en formato String.
     */
    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }
}