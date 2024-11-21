package org.example.vivesbankproject.tarjeta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.utils.IdGenerator;
import org.example.vivesbankproject.utils.TarjetaGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Cache;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "tarjetas")
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {
    private static final Long DEFAULT_ID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = DEFAULT_ID;

    @Column(unique = true)
    @Builder.Default
    private String guid = IdGenerator.generarId();

    @Column(unique = true)
    @Builder.Default
    private String numeroTarjeta = TarjetaGenerator.generarTarjeta();

    @Builder.Default
    private LocalDate fechaCaducidad = LocalDate.now().plusYears(10);

    @Builder.Default
    private Integer cvv = (int) (Math.random() * 900) + 100;

    @Column(nullable = false)
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^[0-9]{3}$", message = "El PIN debe ser un número de 3 dígitos")
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
    @NotBlank(message = "El tipo de tarjeta no puede ser un campo vacío")
    private TipoTarjeta tipoTarjeta;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
