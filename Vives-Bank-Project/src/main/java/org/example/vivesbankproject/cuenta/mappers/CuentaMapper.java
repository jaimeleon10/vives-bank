package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CuentaMapper {

    public CuentaResponse toCuentaResponse(Cuenta cuenta) {
        return CuentaResponse.builder()
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo())
                .tipoCuenta(cuenta.getTipoCuenta())
                .tarjeta(cuenta.getTarjeta())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    public Cuenta toCuenta(CuentaRequest cuentaRequest) {
        return Cuenta.builder()
                .tipoCuenta(cuentaRequest.getTipoCuenta())
                .tarjeta(cuentaRequest.getTarjeta())
                .build();
    }

    public Cuenta toCuentaUpdate(CuentaRequestUpdate cuentaRequestUpdate, Cuenta cuenta) {
        return Cuenta.builder()
                .id(cuenta.getId())
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuentaRequestUpdate.getSaldo())
                .tipoCuenta(cuentaRequestUpdate.getTipoCuenta())
                .tarjeta(cuentaRequestUpdate.getTarjeta())
                .createdAt(cuenta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }
}