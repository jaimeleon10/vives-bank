package org.example.vivesbankproject.movimientos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.ClienteHasNoMovements;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.MovimientoNotFound;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.services.UserService;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.dto.IngresoNominaResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final ClienteService clienteService;
    private final MovimientosRepository movimientosRepository;
    private final DomiciliacionRepository domiciliacionRepository;
    private final CuentaService cuentaService;
    private final MovimientoMapper movimientosMapper;
    private final CuentaMapper cuentaMapper;

    private final UserService userService;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final NotificationMapper notificationMapper;
    private WebSocketHandler webSocketService;



    @Autowired
    public MovimientosServiceImpl(CuentaService cuentaService, MovimientosRepository movimientosRepository, ClienteService clienteService, MovimientoMapper movimientosMapper, DomiciliacionRepository domiciliacionRepository, CuentaMapper cuentaMapper,
            UserService userService,
            WebSocketConfig webSocketConfig,
            NotificationMapper notificationMapper
        ) {
        this.clienteService = clienteService;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientosMapper;
        this.domiciliacionRepository = domiciliacionRepository;
        this.cuentaService = cuentaService;
        this.cuentaMapper = cuentaMapper;

        this.userService = userService;
        this.webSocketConfig = webSocketConfig;

        webSocketService = webSocketConfig.webSocketMovimientosHandler();
        mapper = new ObjectMapper();
        this.notificationMapper = notificationMapper;

        }

    @Override
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosRepository.findAll(pageable).map(movimientosMapper::toMovimientoResponse);
    }


    @Override
    @Cacheable
    public MovimientoResponse getById(ObjectId _id) {
        log.info("Encontrando Movimiento por id: {}", _id);
        return movimientosRepository.findById(_id)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(_id));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByGuid(String guidMovimiento) {
        log.info("Encontrando Movimiento por guid: {}", guidMovimiento);
        return movimientosRepository.findByGuid(guidMovimiento)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(guidMovimiento));
    }

    @Override
    @Cacheable
    public MovimientoResponse getByClienteGuid(String ClienteGuid) {
        log.info("Encontrando Movimientos por idCliente: {}", ClienteGuid);
        clienteService.getById(ClienteGuid);
        return movimientosRepository.findMovimientosByClienteGuid(ClienteGuid)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new ClienteHasNoMovements(ClienteGuid));
    }

    @Override
    @CachePut
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        clienteService.getById(movimientoRequest.getClienteGuid());
        Movimiento movimiento = movimientosMapper.toMovimiento(movimientoRequest);
        var savedMovimiento = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(savedMovimiento);
    }

    @Override
    public Domiciliacion saveDomiciliacion(User user, Domiciliacion domiciliacion) {
        log.info("Guardando Domiciliacion: {}", domiciliacion);

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(domiciliacion.getIbanOrigen());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(domiciliacion.getIbanOrigen());
        }

        // Validar si la domiciliación ya existe
        var clienteDomiciliaciones = domiciliacionRepository.findByClienteGuid(cliente.getGuid());
        if (clienteDomiciliaciones.stream().anyMatch(d -> d.getIbanDestino().equals(domiciliacion.getIbanDestino()))) {
            throw new DuplicatedDomiciliacionException(domiciliacion.getIbanDestino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadDomiciliacion = new BigDecimal(domiciliacion.getCantidad().toString());
        if (cantidadDomiciliacion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad de la domiciliación debe ser mayor a 0");
        }

        // Guardar la domiciliación
        domiciliacion.setUltimaEjecucion(LocalDateTime.now()); // Registro inicial
        domiciliacion.setClienteGuid(cliente.getGuid()); // Asigno el id del cliente al domiciliación
        Domiciliacion saved = domiciliacionRepository.save(domiciliacion);

        // Retornar respuesta
        return saved;
    }


    @Override
    public MovimientoResponse saveIngresoDeNomina(User user, MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento de Ingreso de Nómina: {}", movimientoRequest);

        // Notifaciones
        IngresoDeNomina fakeIngreso = IngresoDeNomina.builder()
                .cantidad(2000.00)
                .iban_Destino("")
                .nombreEmpresa("Mi Empresa")
                .cifEmpresa("").build();
        onChangeIngresoNomina(Notification.Tipo.CREATE,fakeIngreso);


        return null;
    }

    @Override
    public MovimientoResponse savePagoConTarjeta(User user, MovimientoRequest movimientoRequest) {
        return null;
    }

    @Override
    public MovimientoResponse saveTransferencia(User user, MovimientoRequest movimientoRequest) {
        return null;
    }

    void onChangeIngresoNomina(Notification.Tipo tipo, IngresoDeNomina data) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<IngresoNominaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toIngresoNominaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            String clienteId = cuentaService.getByIban(data.getIban_Destino()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            log.info("Enviando mensaje al cliente ws del usuario");
            Thread senderThread = new Thread(() -> {
                try {
                    //webSocketService.sendMessage(json);
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
