package org.example.vivesbankproject.cuenta.dto.cuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaZip {
    private Long id;
    private String guid;
    private String iban;
    private BigDecimal saldo;
    private TipoCuenta tipoCuenta;
    private Tarjeta tarjeta;
    private String clienteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}