package org.example.vivesbankproject.cuenta.services;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaExists;
import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaExists;
import org.example.vivesbankproject.cuenta.exceptions.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
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
@CacheConfig(cacheNames = {"tipo_Cuentas"})
public class TipoCuentaServiceImpl implements TipoCuentaService {
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TipoCuentaMapper tipoCuentaMapper;

    @Autowired
    public TipoCuentaServiceImpl(TipoCuentaRepository tipoCuentaRepository, TipoCuentaMapper tipoCuentaMapper) {
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tipoCuentaMapper = tipoCuentaMapper;
    }

    @Override
    public Page<TipoCuentaResponse> getAll(Optional<String> nombre, Optional<BigDecimal> interes, Pageable pageable) {
        log.info("Obteniendo todos los tipos de cuenta");

        Specification<TipoCuenta> specNombreTipoCuenta = (root, query, criteriaBuilder) ->
                nombre.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> specInteresTipoCuenta = (root, query, criteriaBuilder) ->
                interes.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("interes"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> criterio = Specification.where(specNombreTipoCuenta)
                .and(specInteresTipoCuenta);

        Page<TipoCuenta> tipoCuentaPage = tipoCuentaRepository.findAll(criterio, pageable);

        return tipoCuentaPage.map(tipoCuentaMapper::toTipoCuentaResponse);
    }

    @Override
    @Cacheable
    public TipoCuentaResponse getById(String id) {
        log.info("Obteniendo tipo de cuenta con id: {}", id);
        var tipoCuenta = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta);
    }

    @Override
    @CachePut
    public TipoCuentaResponse save(TipoCuentaRequest tipoCuentaRequest) {
        log.info("Guardando tipo de cuenta: {}", tipoCuentaRequest);
        if (tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre()).isPresent()) {
            throw new TipoCuentaExists(tipoCuentaRequest.getNombre());
        }
        var tipoCuenta = tipoCuentaRepository.save(tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta);
    }

    @Override
    @CachePut
    public TipoCuentaResponse update(String id, TipoCuentaRequest tipoCuentaRequest) {
        log.info("Actualizando tipo de cuenta con id {}", id);
        var tipoCuenta = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        var tipoCuentaSave = tipoCuentaRepository.save(tipoCuentaMapper.toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaSave);
    }

    @Override
    @CacheEvict
    public TipoCuentaResponse deleteById(String id) {
        log.info("Eliminando tipo de cuenta con id {}", id);
        var tipoCuentaExistente = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        tipoCuentaExistente.setIsDeleted(true);
       var tipoCuentaSave= tipoCuentaRepository.save(tipoCuentaExistente);
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaSave);
    }
}
