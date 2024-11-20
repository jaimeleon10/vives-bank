package org.example.vivesbankproject.cuenta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "CUENTAS")
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El numero de cuenta (IBAN) no puede estar vacio")
    private String iban;

    @Column(nullable = false)
    @Digits(integer = 8, fraction = 2, message = "El saldo debe ser un numero valido con hasta dos decimales")
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private Double saldo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_cuenta_id", nullable = false, referencedColumnName = "id")
    private TipoCuenta tipoCuenta;

    @OneToOne(mappedBy = "cuenta", cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
    private Tarjeta tarjeta;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
}