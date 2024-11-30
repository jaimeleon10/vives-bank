package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CuentaMapper {

    public CuentaResponse toCuentaResponse(Cuenta cuenta, String tipoCuentaId, String tarjetaId, String clienteId) {
        return CuentaResponse.builder()
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo().toString())
                .tipoCuentaId(tipoCuentaId)
                .tarjetaId(tarjetaId)
                .clienteId(clienteId)
                .createdAt(cuenta.getCreatedAt().toString())
                .updatedAt(cuenta.getUpdatedAt().toString())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    public Cuenta toCuenta(TipoCuenta tipoCuenta, Tarjeta tarjeta, Cliente cliente) {
        return Cuenta.builder()
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .cliente(cliente)
                .build();
    }

    public Cuenta toCuentaUpdate(CuentaRequestUpdate cuentaRequestUpdate, Cuenta cuenta, TipoCuenta tipoCuenta, Tarjeta tarjeta, Cliente cliente) {
        return Cuenta.builder()
                .id(cuenta.getId())
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuentaRequestUpdate.getSaldo())
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .cliente(cliente)
                .createdAt(cuenta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    public CuentaRequestUpdate toCuentaRequestUpdate(CuentaResponse cuenta) {
        return CuentaRequestUpdate.builder()
                .saldo(new BigDecimal(cuenta.getSaldo()))
                .tipoCuentaId(cuenta.getTipoCuentaId())
                .tarjetaId(cuenta.getTarjetaId())
                .clienteId(cuenta.getClienteId())
                .isDeleted(cuenta.getIsDeleted())
               .build();
    }
}