package org.example.vivesbankproject.tarjeta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "tarjetas")
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El número de tarjeta no puede estar vacío")
    private String numeroTarjeta;

    @Column(nullable = false)
    @Future(message = "La fecha de caducidad debe estar en el futuro")
    private LocalDate fechaCaducidad;

    @Column(nullable = false)
    @NotNull
    @Min(value = 100, message = "El CVV debe ser un número de tres dígitos")
    @Max(value = 999, message = "El CVV debe ser un número de tres dígitos")
    private Integer cvv;

    @Column(nullable = false)
    @NotBlank(message = "El PIN no puede estar vacío")
    private String pin;

    @Column(nullable = false)
    @Positive(message = "El límite diario debe ser un número positivo")
    private BigDecimal limiteDiario;

    @Column(nullable = false)
    @Positive(message = "El límite semanal debe ser un número positivo")
    private BigDecimal limiteSemanal;

    @Column(nullable = false)
    @Positive(message = "El límite mensual debe ser un número positivo")
    private BigDecimal limiteMensual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TipoTarjeta tipoTarjeta;


    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
