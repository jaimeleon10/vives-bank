package org.example.vivesbankproject.cuenta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.utils.IbanGenerator;
import org.example.vivesbankproject.utils.IdGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    private static final Long DEFAULT_ID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = DEFAULT_ID;

    @Builder.Default
    private String guid = IdGenerator.generarId();

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El numero de cuenta (IBAN) no puede estar vacio")
    @Builder.Default
    private String iban = IbanGenerator.generateIban();

    @Column(nullable = false)
    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un numero valido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "tipoCuenta_id", nullable = false, referencedColumnName = "id")
    private TipoCuenta tipoCuenta;

    @OneToOne
    @JoinColumn(name = "tarjeta_id")
    private Tarjeta tarjeta;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}