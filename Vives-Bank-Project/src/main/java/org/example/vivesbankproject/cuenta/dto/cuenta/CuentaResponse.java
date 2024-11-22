package org.example.vivesbankproject.cuenta.dto.cuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponse {
    private String guid;
    private String iban;
    private BigDecimal saldo;
    private TipoCuenta tipoCuenta;
    private Tarjeta tarjeta;
    private Boolean isDeleted;
}