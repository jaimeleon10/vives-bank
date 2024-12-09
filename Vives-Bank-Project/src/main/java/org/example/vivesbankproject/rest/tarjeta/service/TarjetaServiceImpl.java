package org.example.vivesbankproject.rest.tarjeta.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByTarjetaGuid;
import org.example.vivesbankproject.rest.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.rest.tarjeta.dto.*;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaUserPasswordNotValid;
import org.example.vivesbankproject.rest.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.config.websockets.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
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
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de gestión de tarjetas bancarias.
 * Proporciona operaciones CRUD y lógica de negocio para las tarjetas.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"tarjeta"})
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TarjetaMapper tarjetaMapper;
    private final UserRepository userRepository;

    @Setter
    private WebSocketHandler webSocketService;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TarjetaMapper tarjetaMapper, UserRepository userRepository) {
        this.tarjetaRepository = tarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
        this.userRepository = userRepository;
    }


    /**
     * Obtiene todas las tarjetas bancarias que coinciden con los filtros establecidos.
     * La búsqueda se puede filtrar por el número de tarjeta, la fecha de caducidad, el tipo de tarjeta,
     * el límite diario, el límite semanal y el límite mensual. Para cada parámetro se puede especificar
     * un valor mínimo y un valor máximo.
     *
     * @param numero         número de tarjeta
     * @param caducidad      fecha de caducidad
     * @param tipoTarjeta    tipo de tarjeta
     * @param minLimiteDiario límite diario mínimo
     * @param maxLimiteDiario límite diario máximo
     * @param minLimiteSemanal límite semanal mínimo
     * @param maxLimiteSemanal límite semanal máximo
     * @param minLimiteMensual límite mensual mínimo
     * @param maxLimiteMensual límite mensual máximo
     * @param pageable       paginación
     * @return una página de {@link TarjetaResponse} que contiene las tarjetas que coinciden con los filtros
     */
    @Override
    @Operation(
            summary = "Obtener todas las tarjetas",
            description = "Recupera una lista paginada de tarjetas con opciones de filtrado avanzado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tarjetas recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })    
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


    /**
     * Obtiene la tarjeta con el identificador proporcionado.
     *
     * @param id Identificador único de la tarjeta.
     * @return TarjetaResponse con la información de la tarjeta.
     * @throws TarjetaNotFound si no se encuentra una tarjeta con el identificador proporcionado.
     */
    @Override
    @Cacheable
    @Operation(summary = "Obtener tarjeta por ID", description = "Recupera una tarjeta específica mediante su identificador único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta encontrada",
                    content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public TarjetaResponse getById(String id) {
        log.info("Obteniendo la tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    /**
     * Obtiene una tarjeta bancaria por su número de tarjeta.
     *
     * @param numeroTarjeta El número de tarjeta para buscar la tarjeta.
     * @return TarjetaResponse que contiene la información de la tarjeta encontrada.
     * @throws TarjetaNotFoundByNumero si no se encuentra una tarjeta con el número proporcionado.
     */
    @Override
    @Operation(summary = "Obtener tarjeta por número", description = "Recupera una tarjeta específica mediante su número")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta encontrada",
                    content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public TarjetaResponse getByNumeroTarjeta(String numeroTarjeta) {
        log.info("Obteniendo la tarjeta con numero: {}", numeroTarjeta);
        var tarjeta = tarjetaRepository.findByNumeroTarjeta(numeroTarjeta).orElseThrow(() -> new TarjetaNotFoundByNumero(numeroTarjeta));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    /**
     * Obtiene la tarjeta bancaria con los datos privados asociados al usuario que la solicita.
     *
     * @param id Identificador único de la tarjeta.
     * @param tarjetaRequestPrivado Información de la petición de datos privados.
     * @return TarjetaResponsePrivado con la información solicitada.
     * @throws UserNotFoundByUsername si no se encuentra un usuario con el nombre de usuario proporcionado.
     * @throws TarjetaNotFound si no se encuentra una tarjeta con el identificador proporcionado.
     */
    @Override
    @Cacheable
    @Operation(summary = "Obtener datos privados de tarjeta", description = "Recupera datos privados de una tarjeta para un usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos privados recuperados exitosamente",
                    content = @Content(schema = @Schema(implementation = TarjetaResponsePrivado.class))),
            @ApiResponse(responseCode = "404", description = "Tarjeta o usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "Credenciales de usuario inválidas")
    })
    public TarjetaResponsePrivado getPrivateData(String id, TarjetaRequestPrivado tarjetaRequestPrivado) {
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

    /**
     * Guarda una tarjeta bancaria en la base de datos.
     *
     * @param tarjetaRequestSave Información para crear la tarjeta.
     * @return TarjetaResponse con la información de la tarjeta guardada.
     */
    @Override
    @CachePut
    @Operation(summary = "Crear nueva tarjeta", description = "Guarda una nueva tarjeta bancaria en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de tarjeta inválidos")
    })
    public TarjetaResponse save(TarjetaRequestSave tarjetaRequestSave) {
        log.info("Guardando tarjeta: {}", tarjetaRequestSave);
        var tarjeta = tarjetaRepository.save(tarjetaMapper.toTarjeta(tarjetaRequestSave));
        return tarjetaMapper.toTarjetaResponse(tarjeta);
    }

    /**
     * Actualiza una tarjeta bancaria en la base de datos.
     *
     * @param id Identificador único de la tarjeta a actualizar.
     * @param tarjetaRequestUpdate Información para actualizar la tarjeta.
     * @return TarjetaResponse con la información de la tarjeta actualizada.
     * @throws TarjetaNotFound si no se encuentra una tarjeta con el identificador proporcionado.
     */
    @Operation(summary = "Actualizar tarjeta", description = "Actualiza los datos de una tarjeta bancaria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos")
    })
    public TarjetaResponse update(String id, TarjetaRequestUpdate tarjetaRequestUpdate) {
        log.info("Actualizando tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(
                () -> new TarjetaNotFound(id)
        );
        var tarjetaUpdated = tarjetaRepository.save(tarjetaMapper.toTarjetaUpdate(tarjetaRequestUpdate, tarjeta));
        return tarjetaMapper.toTarjetaResponse(tarjetaUpdated);
    }

    /**
     * Marca la tarjeta con el identificador proporcionado como eliminada, lo que implica que no se podrá acceder a ella
     * a través de los métodos de obtención de tarjetas. Sin embargo, la tarjeta sigue estando presente en la base de datos.
     * Notifica el cambio a través de WebSocket.
     * Si no se encuentra una tarjeta con el identificador proporcionado, se lanza una excepción {@link TarjetaNotFound}.
     *
     * @param id Identificador único de la tarjeta a eliminar.
     */
    @Override
    @CacheEvict
    @Operation(summary = "Eliminar tarjeta", description = "Marca una tarjeta como eliminada en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarjeta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    public void deleteById(String id) {
        log.info("Borrando tarjeta con id: {}", id);
        var tarjeta = tarjetaRepository.findByGuid(id).orElseThrow(() -> new TarjetaNotFound(id));
        tarjeta.setIsDeleted(true);
        tarjetaRepository.save(tarjeta);
    }
}