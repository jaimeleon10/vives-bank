package org.example.vivesbankproject.rest.tarjeta.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.example.vivesbankproject.utils.generators.TarjetaGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Cache;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa una entidad de tarjeta bancaria con sus propiedades y configuraciones.
 *
 * Esta clase mapea la tabla de base de datos 'tarjetas' y contiene toda la información
 * relacionada con una tarjeta bancaria, incluyendo detalles de identificación,
 * límites de gasto, y metadatos de creación.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "tarjetas")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa una tarjeta bancaria con sus propiedades completas")
public class Tarjeta {

    /**
     * Identificador interno de la tarjeta en la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador interno de la tarjeta", example = "1")
    private Long id;

    /**
     * Identificador único global (GUID) de la tarjeta.
     * Se genera automáticamente si no se proporciona.
     */
    @Column(unique = true)
    @Builder.Default
    @Schema(description = "Identificador único global de la tarjeta", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid = IdGenerator.generarId();

    /**
     * Número único de la tarjeta.
     * Se genera automáticamente si no se proporciona.
     */
    @Column(unique = true)
    @Builder.Default
    @Schema(description = "Número único de la tarjeta", example = "4111111111111111")
    private String numeroTarjeta = TarjetaGenerator.generarTarjeta();

    /**
     * Fecha de caducidad de la tarjeta.
     * Por defecto, se establece 10 años desde la fecha actual.
     */
    @Builder.Default
    @Schema(description = "Fecha de caducidad de la tarjeta", example = "2034-12-31")
    private LocalDate fechaCaducidad = LocalDate.now().plusYears(10);

    /**
     * Código de Verificación de Valor (CVV) de la tarjeta.
     * Se genera aleatoriamente si no se proporciona.
     */
    @Builder.Default
    @Schema(description = "Código de Verificación de Valor (CVV)", example = "123")
    private Integer cvv = (int) (Math.random() * 900) + 100;

    /**
     * Número de Identificación Personal (PIN) de la tarjeta.
     * Debe ser un número de 4 dígitos.
     */
    @Column(nullable = false)
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un número de 4 dígitos")
    @Schema(description = "PIN de 4 dígitos de la tarjeta", example = "1234")
    private String pin;

    /**
     * Límite de gasto diario de la tarjeta.
     */
    @Column(nullable = false)
    @Positive(message = "El límite diario debe ser un número positivo")
    @Schema(description = "Límite de gasto diario", example = "1000.00")
    private BigDecimal limiteDiario;

    /**
     * Límite de gasto semanal de la tarjeta.
     */
    @Column(nullable = false)
    @Positive(message = "El límite semanal debe ser un número positivo")
    @Schema(description = "Límite de gasto semanal", example = "5000.00")
    private BigDecimal limiteSemanal;

    /**
     * Límite de gasto mensual de la tarjeta.
     */
    @Column(nullable = false)
    @Positive(message = "El límite mensual debe ser un número positivo")
    @Schema(description = "Límite de gasto mensual", example = "20000.00")
    private BigDecimal limiteMensual;

    /**
     * Tipo de tarjeta (crédito o débito).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El tipo de tarjeta no puede ser un campo nulo")
    @Schema(description = "Tipo de tarjeta", example = "CREDITO")
    private TipoTarjeta tipoTarjeta;

    /**
     * Fecha y hora de creación de la tarjeta.
     * Se establece automáticamente al crear la entidad.
     */
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha y hora de creación de la tarjeta", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Fecha y hora de la última actualización de la tarjeta.
     * Se actualiza automáticamente cuando se modifica la entidad.
     */
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha y hora de la última actualización", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Indica si la tarjeta ha sido eliminada lógicamente.
     */
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Indicador de eliminación lógica de la tarjeta", example = "false")
    private Boolean isDeleted = false;
}