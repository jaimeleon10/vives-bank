package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public interface TipoCuentaService {
    Page<TipoCuenta> getAll(Optional<String> nombre, Optional<BigDecimal> interes, Pageable pageable);
    TipoCuenta getById(String id);
    TipoCuenta save(TipoCuenta tipoCuenta);
    TipoCuenta update(String id, TipoCuenta tipoCuenta);
    void deleteById(String id);
}