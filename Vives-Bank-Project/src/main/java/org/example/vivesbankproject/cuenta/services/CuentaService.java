package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CuentaService {
    Page<Cuenta> getAll(Optional<String> iban, Optional<Double> saldo, Optional<Tarjeta> tarjeta, Optional<TipoCuenta> tipoCuenta, Pageable pageable);
    Optional<Cuenta> getById(UUID id);
    Cuenta save(Cuenta cuenta);
    Cuenta update(UUID id, Cuenta cuenta);
    Cuenta delete(UUID id);
}