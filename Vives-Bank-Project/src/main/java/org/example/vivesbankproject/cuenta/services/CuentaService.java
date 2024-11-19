package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CuentaService {
    Page<Cuenta> getAll(Pageable pageable);
    Optional<Cuenta> getById(UUID id);
    Cuenta save(Cuenta cuenta);
    Cuenta update(UUID id, Cuenta cuenta);
    Cuenta deleteById(UUID id);
}