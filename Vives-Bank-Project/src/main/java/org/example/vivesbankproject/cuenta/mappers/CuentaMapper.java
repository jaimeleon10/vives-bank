package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;

public class CuentaMapper {
    public CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo())
                .cliente(cuenta.getCliente())
                .tipoCuenta(cuenta.getTipoCuenta())
                .tarjeta(cuenta.getTarjeta())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }
}
