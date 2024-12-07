package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public interface CuentaService {
    Page<CuentaResponse> getAll(Optional<String> iban, Optional<BigDecimal> saldoMax, Optional<BigDecimal> saldoMin, Optional<String> tipoCuenta, Pageable pageable);

    ArrayList<CuentaResponse> getAllCuentasByClienteGuid(String clienteGuid);

    CuentaResponse getById(String id);

    CuentaResponse getByIban(String iban);

    CuentaResponse getByNumTarjeta(String numTarjeta);

    CuentaResponse save(CuentaRequest cuentaRequest);

    CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate);

    void deleteById(String id);


}