package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public interface TipoCuentaService {
    Page<TipoCuentaResponse> getAll(Optional<String> nombre, Optional<BigDecimal> interes, Pageable pageable);
    TipoCuentaResponse getById(String id);
    TipoCuentaResponse save(TipoCuentaRequest tipoCuentaRequest);
    TipoCuentaResponse update(String id, TipoCuentaRequest tipoCuentaRequest);
    TipoCuentaResponse deleteById(String id);
}