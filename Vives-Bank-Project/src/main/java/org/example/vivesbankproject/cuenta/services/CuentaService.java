package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public interface CuentaService {
    Page<Cuenta> getAll(Optional<String> iban, Optional<BigDecimal> saldoMax, Optional<BigDecimal> saldoMin, Optional<String> tipoCuenta, Pageable pageable);

    CuentaResponse getById(String id);

    CuentaResponse save(CuentaRequest cuentaRequest);

    CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate);

    void delete(String id);
}