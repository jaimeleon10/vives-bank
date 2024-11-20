package org.example.vivesbankproject.cuenta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "cuentas")
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El numero de cuenta (IBAN) no puede estar vacio")
    @Builder.Default
    private String iban = generateIban();

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

    private static String generateIban() {
        String countryCode = "ES";

        String bankCode = "1234";
        String branchCode = "1234";
        String controlDigits = String.format("%02d", (int)(Math.random() * 100));
        String accountNumber = String.format("%010d", (int)(Math.random() * 1_000_000_0000L));

        String tempIban = bankCode + branchCode + controlDigits + accountNumber + "142800";

        String numericIban = tempIban.chars()
                .mapToObj(c -> Character.isDigit(c) ? String.valueOf((char) c) : String.valueOf(c - 'A' + 10))
                .reduce("", String::concat);

        int checksum = 98 - (new java.math.BigInteger(numericIban).mod(java.math.BigInteger.valueOf(97)).intValue());

        return countryCode + String.format("%02d", checksum) + bankCode + branchCode + controlDigits + accountNumber;
    }
}