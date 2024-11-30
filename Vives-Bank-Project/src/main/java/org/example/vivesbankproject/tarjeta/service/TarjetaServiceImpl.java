package org.example.vivesbankproject.tarjeta.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByIban;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByTarjeta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaUserPasswordNotValid;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.dto.IngresoNominaResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
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

@Slf4j
@Service
@CacheConfig(cacheNames = {"tarjeta"})
public class TarjetaServiceImpl implements TarjetaService {

    private final TarjetaRepository tarjetaRepository;
    private final TarjetaMapper tarjetaMapper;
    private final UserRepository userRepository;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final CuentaRepository cuentaRepository;
    //private final NotificationMapper notificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public TarjetaServiceImpl(TarjetaRepository tarjetaRepository, TarjetaMapper tarjetaMapper, UserRepository userRepository, WebSocketConfig webSocketConfig, CuentaRepository cuentaRepository) {
     //       ,NotificationMapper notificationMapper
        this.tarjetaRepository = tarjetaRepository;
        this.tarjetaMapper = tarjetaMapper;
        this.userRepository = userRepository;

        this.webSocketConfig = webSocketConfig;

        webSocketService = webSocketConfig.webSocketTarjetasHandler();
        mapper = new ObjectMapper();
        //this.notificationMapper = notificationMapper;
        this.cuentaRepository = cuentaRepository;
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

    void onChange(Notification.Tipo tipo, Tarjeta data) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketTarjetasHandler();
        }

        try {
            Notification<TarjetaResponse> notificacion = new Notification<>(
                    "TARJETAS",
                    tipo,
                    tarjetaMapper.toTarjetaResponse(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el usuario del cliente de la tarjeta
            var cuenta = cuentaRepository.findByTarjetaId(data.getId()).orElseThrow(() -> new CuentaNotFoundByTarjeta(data.getId()));
            String userId = cuenta.getCliente().getUser().getId().toString();
            User user = userRepository.findByGuid(userId).orElseThrow(() -> new UserNotFoundById(userId));
            String userName = user.getUsername();

            log.info("Enviando mensaje al cliente ws del usuario");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessageToUser(userName,json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    // Para los test
    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}