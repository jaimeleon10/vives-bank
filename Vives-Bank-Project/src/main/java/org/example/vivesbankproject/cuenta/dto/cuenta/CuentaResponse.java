package org.example.vivesbankproject.cuenta.dto.cuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.dto.ClienteForCuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponse {
    private String guid;
    private String iban;
    private BigDecimal saldo;
    private TipoCuentaResponse tipoCuenta;
    private TarjetaResponse tarjeta;
    private ClienteForCuentaResponse cliente;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}