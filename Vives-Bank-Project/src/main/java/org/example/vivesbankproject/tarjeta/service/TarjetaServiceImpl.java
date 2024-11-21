package org.example.vivesbankproject.tarjeta.service;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TarjetaMapper tarjetaMapper) {
        this.tarjetaRepository = tarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
    }

    @Override
    public Page<TarjetaResponse> getAll(Optional<String> numero, Optional<LocalDate> caducidad,
                                        Optional<TipoTarjeta> tipoTarjeta,
                                        Optional<Double> limiteDiario,
                                        Optional<Double> limiteSemanal,
                                        Optional<Double> limiteMensual,
                                        Pageable pageable) {

        Specification<Tarjeta> specNumero = (root, query, criteriaBuilder) ->
                numero.map(value -> criteriaBuilder.like(root.get("numeroTarjeta"), "%" + value + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specCaducidad = (root, query, criteriaBuilder) ->
                caducidad.map(value -> criteriaBuilder.equal(root.get("fechaCaducidad"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specTipoTarjeta = (root, query, criteriaBuilder) ->
                tipoTarjeta.map(value -> criteriaBuilder.equal(root.get("tipoTarjeta"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specLimiteDiario = (root, query, criteriaBuilder) ->
                limiteDiario.map(value -> criteriaBuilder.equal(root.get("limiteDiario"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specLimiteSemanal = (root, query, criteriaBuilder) ->
                limiteSemanal.map(value -> criteriaBuilder.equal(root.get("limiteSemanal"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> specLimiteMensual = (root, query, criteriaBuilder) ->
                limiteMensual.map(value -> criteriaBuilder.equal(root.get("limiteMensual"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Tarjeta> criteria = Specification.where(specNumero)
                .and(specCaducidad)
                .and(specTipoTarjeta)
                .and(specLimiteDiario)
                .and(specLimiteSemanal)
                .and(specLimiteMensual);

        Page<Tarjeta> tarjetasPage = tarjetaRepository.findAll(criteria, pageable);

        List<TarjetaResponse> tarjetaResponses = tarjetasPage.getContent().stream()
                .map(tarjetaMapper::toTarjetaResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(tarjetaResponses, pageable, tarjetasPage.getTotalElements());
    }


    @Override
    public TarjetaResponse getById(String id) {
        log.info("Obteniendo la tarjeta con ID: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    @Override
    public TarjetaResponse save(TarjetaRequest tarjetaRequest) {
        log.info("Guardando tarjeta: {}", tarjetaRequest);
        var tarjeta = tarjetaMapper.toTarjeta(tarjetaRequest);
        tarjeta.setTipoTarjeta(tarjetaRequest.getTipoTarjeta());
        var savedTarjeta = tarjetaRepository.save(tarjeta);
        return tarjetaMapper.toTarjetaResponse(savedTarjeta);
    }

    @Override
    public TarjetaResponse update(String id, TarjetaRequest tarjetaRequest) {
        log.info("Actualizando tarjeta con id: {}", id);
        var existingTarjeta = tarjetaRepository.findByGuid(id)
                .orElseThrow(() -> new TarjetaNotFound(id));

        var tarjeta = tarjetaMapper.toTarjeta(tarjetaRequest);
        tarjeta.setTipoTarjeta(tarjetaRequest.getTipoTarjeta());
        tarjeta.setId(existingTarjeta.getId());
        var updatedTarjeta = tarjetaRepository.save(tarjeta);
        return tarjetaMapper.toTarjetaResponse(updatedTarjeta);
    }

    @Override
    public TarjetaResponse deleteById(String id) {
        log.info("Eliminando tarjeta con ID: {}", id);
        var tarjetaExistente = tarjetaRepository.findByGuid(id)
                .orElseThrow(() -> new TarjetaNotFound(id));

        tarjetaRepository.deleteById(tarjetaExistente.getId());
        return tarjetaMapper.toTarjetaResponse(tarjetaExistente);
    }
}