package org.example.vivesbankproject.rest.cuenta.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Cache;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa la entidad TipoCuenta en la base de datos, utilizada para mapear la información
 * de los tipos de cuenta en la aplicación. Se utiliza para interactuar con la base de datos mediante JPA.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "tipo_Cuenta")
@NoArgsConstructor
@AllArgsConstructor
public class TipoCuenta {

    /**
     * Identificador único de cada tipo de cuenta en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del tipo de cuenta en la base de datos.", example = "1")
    private Long id;

    /**
     * Identificador único global generado automáticamente para el tipo de cuenta.
     */
    @Builder.Default
    @Schema(description = "Identificador único global generado automáticamente para el tipo de cuenta.", example = "abc12345")
    private String guid = IdGenerator.generarId();

    /**
     * Nombre del tipo de cuenta, debe ser único y no puede estar vacío.
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre del tipo de cuenta no puede estar vacío")
    @Schema(description = "Nombre del tipo de cuenta. No puede estar vacío y debe ser único.", example = "Cuenta de ahorro")
    private String nombre;

    /**
     * Tasa de interés para el tipo de cuenta, debe ser positiva o cero y un número válido con hasta dos decimales.
     */
    @Column(nullable = false)
    @Digits(integer = 3, fraction = 2, message = "El interés debe ser un número válido")
    @PositiveOrZero(message = "El interés no puede ser negativo")
    @Schema(description = "Tasa de interés para el tipo de cuenta. Debe ser positiva o cero y un número válido con hasta dos decimales.", example = "2.50")
    private BigDecimal interes;

    /**
     * Fecha de creación del tipo de cuenta. Se establece automáticamente en el momento de la creación.
     */
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de creación del tipo de cuenta.", example = "2023-12-07T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Fecha de última actualización del tipo de cuenta. Se actualiza automáticamente en cada operación de actualización.
     */
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de última actualización del tipo de cuenta.", example = "2023-12-07T12:30:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Indicador de borrado lógico para el tipo de cuenta. Por defecto es falso.
     */
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Indica si el tipo de cuenta ha sido borrado lógicamente.", example = "false")
    private Boolean isDeleted = false;
}