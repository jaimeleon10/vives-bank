package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CuentaMapper {

    public CuentaResponse toCuentaResponse(Cuenta cuenta, TipoCuentaResponse tipoCuentaResponse, TarjetaResponse tarjetaResponse) {
        return CuentaResponse.builder()
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo())
                .tipoCuenta(tipoCuentaResponse)
                .tarjeta(tarjetaResponse)
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    public Cuenta toCuenta(TipoCuenta tipoCuenta, Tarjeta tarjeta) {
        return Cuenta.builder()
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .build();
    }

    public Cuenta toCuentaUpdate(CuentaRequestUpdate cuentaRequestUpdate, Cuenta cuenta, TipoCuenta tipoCuenta, Tarjeta tarjeta) {
        return Cuenta.builder()
                .id(cuenta.getId())
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuentaRequestUpdate.getSaldo())
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .createdAt(cuenta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }
}