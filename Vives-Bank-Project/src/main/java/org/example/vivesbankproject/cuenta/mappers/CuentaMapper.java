package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CuentaMapper {

    public CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo())
                .tipoCuenta(cuenta.getTipoCuenta())
                .tarjeta(cuenta.getTarjeta())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    public Cuenta toCuenta(CuentaRequest cuentaRequest) {
        return Cuenta.builder()
                .iban(cuentaRequest.getIban())
                .saldo(cuentaRequest.getSaldo())
                .tipoCuenta(cuentaRequest.getTipoCuenta())
                .tarjeta(cuentaRequest.getTarjeta())
                .isDeleted(cuentaRequest.getIsDeleted())
                .build();
    }
}
