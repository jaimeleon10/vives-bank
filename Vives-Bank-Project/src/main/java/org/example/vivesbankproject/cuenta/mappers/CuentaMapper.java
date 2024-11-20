package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CuentaMapper {
    public Cuenta toCuentaUpdate(Cuenta cuenta){
        return new Cuenta(
                cuenta.getId(),
                cuenta.getIban(),
                cuenta.getSaldo(),
                cuenta.getTipoCuenta(),
                cuenta.getTarjeta(),
                cuenta.getCreatedAt(),
                LocalDateTime.now(),
                cuenta.getIsDeleted()
        );
    }

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
}
