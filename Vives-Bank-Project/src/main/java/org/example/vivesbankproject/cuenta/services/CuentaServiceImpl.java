package org.example.vivesbankproject.cuenta.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByIban;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByNumTarjeta;
import org.example.vivesbankproject.cuenta.exceptions.tipoCuenta.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"cuenta"})
public class CuentaServiceImpl implements CuentaService{
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;

    private final UserRepository userRepository;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    // Para los test
    @Setter
    private WebSocketHandler webSocketService;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper, TipoCuentaRepository tipoCuentaRepository, TarjetaRepository tarjetaRepository, ClienteRepository clienteRepository, WebSocketConfig webSocketConfig, UserRepository userRepository) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.clienteRepository = clienteRepository;

        this.userRepository = userRepository;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketTarjetasHandler();
        mapper = new ObjectMapper();
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

        Specification<Cuenta> specTipoCuenta = (root, query, criteriaBuilder) ->
                tipoCuenta.map(t -> {
                    Join<Cuenta, TipoCuenta> tipoCuentaJoin = root.join("tipoCuenta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tipoCuentaJoin.get("nombre")), "%" + t.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> criterio = Specification.where(specIbanCuenta)
                .and(specSaldoMaxCuenta)
                .and(specSaldoMinCuenta)
                .and(specTipoCuenta);

        Page<Cuenta> cuentaPage = cuentaRepository.findAll(criterio, pageable);

        return cuentaPage.map(cuenta ->
                cuentaMapper.toCuentaResponse(
                        cuenta,
                        cuenta.getTipoCuenta().getGuid(),
                        cuenta.getTarjeta().getGuid(),
                        cuenta.getCliente().getGuid()
                )
        );
    }

    @Override
    public ArrayList<CuentaResponse> getAllCuentasByClienteGuid(String clienteGuid) {
        ArrayList<Cuenta> cuentas = cuentaRepository.findAllByCliente_Guid(clienteGuid);

        ArrayList<CuentaResponse> cuentaResponses = new ArrayList<>();
        cuentas.forEach(cuenta -> {
            CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(
                    cuenta,
                    cuenta.getTipoCuenta().getGuid(),
                    cuenta.getTarjeta().getGuid(),
                    cuenta.getCliente().getGuid()
            );
            cuentaResponses.add(cuentaResponse);
        });

        return cuentaResponses;
    }

    @Override
    @Cacheable
    public CuentaResponse getById(String id) {
        log.info("Obteniendo la cuenta con id: {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(() -> new CuentaNotFound(id));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    @Override
    public CuentaResponse getByIban(String iban) {
        var cuenta = cuentaRepository.findByIban(iban).orElseThrow(() -> new CuentaNotFoundByIban(iban));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    @Override
    public CuentaResponse getByNumTarjeta(String numTarjeta) {
        var tarjeta = tarjetaRepository.findByNumeroTarjeta(numTarjeta).orElseThrow(() -> new TarjetaNotFoundByNumero(numTarjeta));
        var cuenta = cuentaRepository.findByTarjetaId(tarjeta.getId()).orElseThrow(() -> new CuentaNotFoundByNumTarjeta(tarjeta.getId().toString()));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    @Override
    @CachePut
    public CuentaResponse save(CuentaRequest cuentaRequest) {
        log.info("Guardando cuenta: {}", cuentaRequest);
        var tipoCuenta = tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId()).orElseThrow(
                () -> new TipoCuentaNotFound(cuentaRequest.getTipoCuentaId())
        );
        var tarjeta = tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId()).orElseThrow(
                () -> new TarjetaNotFound(cuentaRequest.getTarjetaId())
        );
        var cliente = clienteRepository.findByGuid(cuentaRequest.getClienteId()).orElseThrow(
                () -> new ClienteNotFound(cuentaRequest.getClienteId())
        );
        var cuentaSaved = cuentaRepository.save(cuentaMapper.toCuenta(tipoCuenta, tarjeta, cliente));

        // Actualizamos el listado de cuentas del cliente
        cliente.getCuentas().add(cuentaSaved);
        clienteRepository.save(cliente);

        // Forzamos sincronización y evitamos cache en siguiente busqueda de cliente
        clienteRepository.flush();
        evictClienteCache(cliente.getGuid());

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaSaved, cuentaSaved.getTipoCuenta().getGuid(), cuentaSaved.getTarjeta().getGuid(), cuentaSaved.getCliente().getGuid());

        onChange(Notification.Tipo.CREATE, cuentaResponse, cuentaSaved);
        return cuentaMapper.toCuentaResponse(cuentaSaved, cuentaSaved.getTipoCuenta().getGuid(), cuentaSaved.getTarjeta().getGuid(), cuentaSaved.getCliente().getGuid());
    }

    @Override
    @CachePut
    public CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate) {
        log.info("Actualizando cuenta con id {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );

        var tipoCuenta = cuenta.getTipoCuenta();
        var tarjeta = cuenta.getTarjeta();
        var cliente = cuenta.getCliente();

        if (!cuentaRequestUpdate.getTipoCuentaId().isEmpty()) {
            tipoCuenta = tipoCuentaRepository.findByGuid(cuentaRequestUpdate.getTipoCuentaId()).orElseThrow(
                    () -> new TipoCuentaNotFound(cuentaRequestUpdate.getTipoCuentaId())
            );
        }

        if (!cuentaRequestUpdate.getTarjetaId().isEmpty()) {
            tarjeta = tarjetaRepository.findByGuid(cuentaRequestUpdate.getTarjetaId()).orElseThrow(
                    () -> new TarjetaNotFound(cuentaRequestUpdate.getTarjetaId())
            );
        }

        if (!cuentaRequestUpdate.getClienteId().isEmpty()) {
            cliente = clienteRepository.findByGuid(cuentaRequestUpdate.getClienteId()).orElseThrow(
                    () -> new ClienteNotFound(cuentaRequestUpdate.getClienteId())
            );
        }

        var cuentaUpdated = cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, tipoCuenta, tarjeta, cliente));

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaUpdated, cuentaUpdated.getTipoCuenta().getGuid(), cuentaUpdated.getTarjeta().getGuid(), cuentaUpdated.getCliente().getGuid());

        onChange(Notification.Tipo.UPDATE, cuentaResponse, cuentaUpdated);
        return cuentaMapper.toCuentaResponse(cuentaUpdated, cuentaUpdated.getTipoCuenta().getGuid(), cuentaUpdated.getTarjeta().getGuid(), cuentaUpdated.getCliente().getGuid());
    }

    @Override
    @CacheEvict
    public void deleteById(String id) {
        log.info("Eliminando cuenta con id {}", id);
        var cuentaExistente = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );
        cuentaExistente.setIsDeleted(true);
        cuentaRepository.save(cuentaExistente);

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaExistente, cuentaExistente.getTipoCuenta().getGuid(), cuentaExistente.getTarjeta().getGuid(), cuentaExistente.getCliente().getGuid());

        onChange(Notification.Tipo.DELETE, cuentaResponse, cuentaExistente);
    }

    @CacheEvict
    public void evictClienteCache(String clienteGuid) {
        log.info("Invalidando la caché del cliente con GUID: {}", clienteGuid);
    }


    void onChange(Notification.Tipo tipo, CuentaResponse data, Cuenta cuenta) {
        log.info("Servicio de Cuentas onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketCuentasHandler();
        }

        try {
            Notification<CuentaResponse> notificacion = new Notification<>(
                    "CUENTAS",
                    tipo,
                    cuentaMapper.toCuentaResponse(
                            cuenta,
                            data.getTipoCuentaId(),
                            data.getTarjetaId(),
                            data.getClienteId()
                            ),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el usuario del cliente de la cuenta
            String userId = cuenta.getCliente().getUser().getGuid();
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
}