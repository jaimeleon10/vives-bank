package org.example.vivesbankproject.tarjeta.service;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaUserPasswordNotValid;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"tarjeta"})
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TarjetaMapper tarjetaMapper;
    private final UserRepository userRepository;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TarjetaMapper tarjetaMapper, UserRepository userRepository) {
        this.tarjetaRepository = tarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Page<TarjetaResponse> getAll(Optional<String> numero,
                                Optional<LocalDate> caducidad,
                                Optional<TipoTarjeta> tipoTarjeta,
                                Optional<BigDecimal> minLimiteDiario,
                                Optional<BigDecimal> maxLimiteDiario,
                                Optional<BigDecimal> minLimiteSemanal,
                                Optional<BigDecimal> maxLimiteSemanal,
                                Optional<BigDecimal> minLimiteMensual,
                                Optional<BigDecimal> maxLimiteMensual,
                                Pageable pageable) {
        log.info("Obteniendo todas las tarjetas");

        Specification<Tarjeta> specNumero = (root, query, criteriaBuilder) ->
                numero.map(value -> criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroTarjeta")), "%" + value.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specCaducidad = (root, query, criteriaBuilder) ->
                caducidad.map(value -> criteriaBuilder.equal(root.get("fechaCaducidad"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specTipoTarjeta = (root, query, criteriaBuilder) ->
                tipoTarjeta.map(value -> criteriaBuilder.equal(root.get("tipoTarjeta"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteDiario = (root, query, criteriaBuilder) ->
                minLimiteDiario.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteDiario"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteDiario = (root, query, criteriaBuilder) ->
                maxLimiteDiario.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteDiario"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteSemanal = (root, query, criteriaBuilder) ->
                minLimiteSemanal.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteSemanal"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteSemanal = (root, query, criteriaBuilder) ->
                maxLimiteSemanal.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteSemanal"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMinLimiteMensual = (root, query, criteriaBuilder) ->
                minLimiteMensual.map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("limiteMensual"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specMaxLimiteMensual = (root, query, criteriaBuilder) ->
                maxLimiteMensual.map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("limiteMensual"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> criteria = Specification.where(specNumero)
                .and(specCaducidad)
                .and(specTipoTarjeta)
                .and(specMinLimiteDiario)
                .and(specMaxLimiteDiario)
                .and(specMinLimiteSemanal)
                .and(specMaxLimiteSemanal)
                .and(specMinLimiteMensual)
                .and(specMaxLimiteMensual);

        Page<Tarjeta> tarjetaPage = tarjetaRepository.findAll(criteria, pageable);

        return tarjetaPage.map(tarjetaMapper::toTarjetaResponse);
    }


    @Override
    @Cacheable
    public TarjetaResponse getById(String id) {
        log.info("Obteniendo la tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    public TarjetaResponse getByNumeroTarjeta(String numeroTarjeta) {
        log.info("Obteniendo la tarjeta con numero: {}", numeroTarjeta);
        var tarjeta = tarjetaRepository.findByNumeroTarjeta(numeroTarjeta).orElseThrow(() -> new TarjetaNotFoundByNumero(numeroTarjeta));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    @Cacheable
    public TarjetaResponsePrivado getPrivateData(String id, TarjetaRequestPrivado tarjetaRequestPrivado) {
        // Cambiar cuando añadamos autenticación
        log.info("Obteniendo datos privados de la tarjeta con id: {}", id);
        var user = userRepository.findByUsername(tarjetaRequestPrivado.getUsername());
        if (user.isEmpty()) {
            throw new UserNotFoundByUsername(tarjetaRequestPrivado.getUsername());
        } else {
            if (!user.get().getPassword().equals(tarjetaRequestPrivado.getUserPass())) {
                throw new TarjetaUserPasswordNotValid();
            } else {
                var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
                return tarjetaMapper.toTarjetaPrivado(tarjeta);
            }
        }
    }

    @Override
    @CachePut
    public TarjetaResponse save(TarjetaRequestSave tarjetaRequestSave) {
        log.info("Guardando tarjeta: {}", tarjetaRequestSave);
        var tarjeta = tarjetaRepository.save(tarjetaMapper.toTarjeta(tarjetaRequestSave));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    @CachePut
    public TarjetaResponse update(String id, TarjetaRequestUpdate tarjetaRequestUpdate) {
        log.info("Actualizando tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(
                () -> new TarjetaNotFound(id)
        );
        var tarjetaUpdated = tarjetaRepository.save(tarjetaMapper.toTarjetaUpdate(tarjetaRequestUpdate, tarjeta));
        return tarjetaMapper.toTarjetaResponse(tarjetaUpdated);
    }

    @Override
    @CacheEvict
    public void deleteById(String id) {
        log.info("Borrando tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        tarjeta.setIsDeleted(true);
        tarjetaRepository.save(tarjeta);
    }
}