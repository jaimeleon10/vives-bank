package org.example.vivesbankproject.rest.cuenta.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaExists;
import org.example.vivesbankproject.rest.cuenta.exceptions.tipoCuenta.TipoCuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
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

/**
 * Implementación del servicio para realizar operaciones con los tipos de cuenta.
 * Implementa la lógica de negocio para acceder, actualizar, eliminar y crear
 * información sobre los tipos de cuenta.
 * Se interactúa con el repositorio y el mapper correspondiente para mapear entidades a DTOs.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
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
    @Operation(summary = "Obtener todos los tipos de cuenta con filtros opcionales",
            description = "Devuelve una página de resultados de tipos de cuenta aplicando filtros opcionales como nombre, interés máximo y mínimo.")
    @Parameter(name = "nombre", description = "Filtro opcional por nombre del tipo de cuenta", required = false)
    @Parameter(name = "interesMax", description = "Filtro opcional para el interés máximo", required = false)
    @Parameter(name = "interesMin", description = "Filtro opcional para el interés mínimo", required = false)
    public Page<TipoCuentaResponse> getAll(Optional<String> nombre, Optional<BigDecimal> interesMax, Optional<BigDecimal> interesMin, Pageable pageable) {
        log.info("Obteniendo todos los tipos de cuenta");

        Specification<TipoCuenta> specNombreTipoCuenta = (root, query, criteriaBuilder) ->
                nombre.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> specInteresMaxTipoCuenta = (root, query, criteriaBuilder) ->
                interesMax.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("interes"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> specInteresMinTipoCuenta = (root, query, criteriaBuilder) ->
                interesMin.map(s -> criteriaBuilder.greaterThanOrEqualTo(root.get("interes"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<TipoCuenta> criterio = Specification.where(specNombreTipoCuenta)
                .and(specInteresMaxTipoCuenta)
                .and(specInteresMinTipoCuenta);

        Page<TipoCuenta> tipoCuentaPage = tipoCuentaRepository.findAll(criterio, pageable);

        return tipoCuentaPage.map(tipoCuentaMapper::toTipoCuentaResponse);
    }

    @Override
    @Cacheable
    @Operation(summary = "Obtener un tipo de cuenta por su identificador",
            description = "Obtiene un tipo de cuenta específico por su identificador (GUID).")
    @Parameter(name = "id", description = "Identificador único del tipo de cuenta", required = true)
    public TipoCuentaResponse getById(String id) {
        log.info("Obteniendo tipo de cuenta con id: {}", id);
        var tipoCuenta = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta);
    }

    @Override
    @CachePut
    @Operation(summary = "Guardar un nuevo tipo de cuenta",
            description = "Crea un nuevo tipo de cuenta en la base de datos.")
    @Parameter(name = "tipoCuentaRequest", description = "Información para crear un nuevo tipo de cuenta", required = true)
    public TipoCuentaResponse save(TipoCuentaRequest tipoCuentaRequest) {
        log.info("Guardando tipo de cuenta: {}", tipoCuentaRequest);
        if (tipoCuentaRepository.findByNombre(tipoCuentaRequest.getNombre()).isPresent()) {
            throw new TipoCuentaExists(tipoCuentaRequest.getNombre());
        }
        var tipoCuenta = tipoCuentaRepository.save(tipoCuentaMapper.toTipoCuenta(tipoCuentaRequest));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuenta);
    }

    @Override
    @Operation(summary = "Actualizar un tipo de cuenta existente",
            description = "Actualiza la información de un tipo de cuenta utilizando su identificador.")
    @Parameter(name = "id", description = "Identificador del tipo de cuenta a actualizar", required = true)
    @Parameter(name = "tipoCuentaRequest", description = "Información para actualizar el tipo de cuenta", required = true)
    @CachePut
    public TipoCuentaResponse update(String id, TipoCuentaRequest tipoCuentaRequest) {
        log.info("Actualizando tipo de cuenta con id {}", id);
        var tipoCuenta = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        var tipoCuentaSave = tipoCuentaRepository.save(tipoCuentaMapper.toTipoCuentaUpdate(tipoCuentaRequest, tipoCuenta));
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaSave);
    }

    @Override
    @Operation(summary = "Eliminar un tipo de cuenta por su identificador",
            description = "Elimina un tipo de cuenta marcándola como eliminada en el sistema.")
    @Parameter(name = "id", description = "Identificador del tipo de cuenta a eliminar", required = true)
    @CacheEvict
    public TipoCuentaResponse deleteById(String id) {
        log.info("Borrando tipo de cuenta con id {}", id);
        var tipoCuentaExistente = tipoCuentaRepository.findByGuid(id).orElseThrow(() -> new TipoCuentaNotFound(id));
        tipoCuentaExistente.setIsDeleted(true);
       var tipoCuentaSave= tipoCuentaRepository.save(tipoCuentaExistente);
        return tipoCuentaMapper.toTipoCuentaResponse(tipoCuentaSave);
    }
}
