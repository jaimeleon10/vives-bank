package org.example.vivesbankproject.cuenta.services;

import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"cuenta"})
public class CuentaServiceImpl implements CuentaService{
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    public Page<CuentaResponse> getAll(Optional<String> iban, Optional<BigDecimal> saldoMax, Optional<BigDecimal> saldoMin, Optional<String> tipoCuenta, Pageable pageable) {
        log.info("Obteniendo todas las cuentas");

        Specification<Cuenta> specIbanCuenta = (root, query, criteriaBuilder) ->
                iban.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("iban")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMaxCuenta = (root, query, criteriaBuilder) ->
                saldoMax.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMinCuenta = (root, query, criteriaBuilder) ->
                saldoMin.map(s -> criteriaBuilder.greaterThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specTipoCuentaFunko = (root, query, criteriaBuilder) ->
                tipoCuenta.map(t -> {
                    Join<Cuenta, TipoCuenta> tipoCuentaJoin = root.join("tipoCuenta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tipoCuentaJoin.get("nombre")), "%" + t.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> criterio = Specification.where(specIbanCuenta)
                .and(specSaldoMaxCuenta)
                .and(specSaldoMinCuenta)
                .and(specTipoCuentaFunko);

        Page<Cuenta> cuentaPage = cuentaRepository.findAll(criterio, pageable);

        return cuentaPage.map(cuentaMapper::toCuentaResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public CuentaResponse getById(String id) {
        log.info("Obteniendo la cuenta con id: {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(() -> new CuentaNotFound(id));
        return cuentaMapper.toCuentaResponse(cuenta);
    }

    @Override
    @CachePut(key = "#result.guid")
    public CuentaResponse save(CuentaRequest cuentaRequest) {
        log.info("Guardando cuenta: {}", cuentaRequest);
        var cuenta = cuentaRepository.save(cuentaMapper.toCuenta(cuentaRequest));
        return cuentaMapper.toCuentaResponse(cuenta);
    }

    @Override
    @CachePut(key = "#result.guid")
    public CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate) {
        log.info("Actualizando cuenta con id {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(() -> new CuentaNotFound(id));
        var cuentaSaved = cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta));
        return cuentaMapper.toCuentaResponse(cuentaSaved);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("Eliminando cuenta con id {}", id);
        var cuentaExistente = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );
        cuentaExistente.setIsDeleted(true);
        cuentaRepository.save(cuentaExistente);
    }
}