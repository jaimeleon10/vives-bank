package org.example.vivesbankproject.rest.cuenta.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.utils.generators.IbanGenerator;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Cache;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa la entidad Cuenta en la base de datos, utilizada para mapear la información de las cuentas
 * en la aplicación. Esta clase se utiliza para interactuar con la base de datos mediante JPA.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"cliente"})
public class Cuenta {

    /**
     * Identificador único para cada cuenta, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la cuenta en la base de datos.", example = "1")
    private Long id;

    /**
     * Identificador único global de la cuenta generado automáticamente.
     */
    @Builder.Default
    @Schema(description = "Identificador global único generado para la cuenta.", example = "abc12345")
    private String guid = IdGenerator.generarId();

    /**
     * Código IBAN único para la cuenta, generado automáticamente.
     */
    @Column(unique = true)
    @Builder.Default
    @Schema(description = "Código IBAN único para la cuenta, generado automáticamente.", example = "ES91110000002012345678901234")
    private String iban = IbanGenerator.generateIban();

    /**
     * Saldo de la cuenta con restricciones: debe ser un número válido con hasta dos decimales,
     * y no puede ser negativo.
     */
    @Column(nullable = false)
    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un numero valido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    @Builder.Default
    @Schema(description = "Saldo de la cuenta. No puede ser negativo y debe tener hasta dos decimales.", example = "1000.00")
    private BigDecimal saldo = BigDecimal.valueOf(0);

    /**
     * Relación con el tipo de cuenta asociado.
     */
    @ManyToOne
    @JoinColumn(name = "tipoCuenta_id", nullable = false, referencedColumnName = "id")
    @Schema(description = "Tipo de cuenta asociada a la cuenta.", example = "1")
    private TipoCuenta tipoCuenta;

    /**
     * Relación con la tarjeta asociada a la cuenta.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tarjeta_id", referencedColumnName = "id")
    @Schema(description = "Tarjeta asociada a la cuenta.")
    private Tarjeta tarjeta;

    /**
     * Relación con el cliente asociado a la cuenta. Es obligatorio tener un cliente asignado.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "La cuenta debe estar asociada a un cliente")
    @JsonIgnore
    @Schema(description = "Cliente asociado a la cuenta.", example = "1")
    private Cliente cliente;

    /**
     * Fecha de creación de la cuenta, establecida automáticamente al momento de la creación.
     */
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de creación de la cuenta.", example = "2023-12-07T12:00:00")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Fecha de última actualización de la cuenta, establecida automáticamente en cada operación de actualización.
     */
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de última actualización de la cuenta.", example = "2023-12-07T12:30:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Indicador de borrado lógico de la cuenta. Por defecto, es falso.
     */
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Indica si la cuenta ha sido borrada lógicamente.", example = "false")
    private Boolean isDeleted = false;
}