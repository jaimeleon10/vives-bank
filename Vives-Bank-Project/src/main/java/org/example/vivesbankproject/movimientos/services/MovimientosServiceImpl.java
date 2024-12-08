package org.example.vivesbankproject.movimientos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import org.example.vivesbankproject.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByClienteGuid;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByTarjetaId;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.movimientos.exceptions.movimientos.*;
import org.example.vivesbankproject.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.movimientos.models.*;
import org.example.vivesbankproject.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.services.UserService;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.dto.DomiciliacionResponse;
import org.example.vivesbankproject.websocket.notifications.dto.IngresoNominaResponse;
import org.example.vivesbankproject.websocket.notifications.dto.PagoConTarjetaResponse;
import org.example.vivesbankproject.websocket.notifications.dto.TransferenciaResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.example.vivesbankproject.utils.validators.ValidarCif;
import org.example.vivesbankproject.utils.validators.ValidarIban;
import org.example.vivesbankproject.utils.validators.ValidarTarjeta;
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
    private final TarjetaService tarjetaService;
    private final CuentaMapper cuentaMapper;
    private final UserService userService;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final NotificationMapper notificationMapper;
    // Para los test
    @Setter
    private WebSocketHandler webSocketService;



    @Autowired
    public MovimientosServiceImpl( CuentaService cuentaService, MovimientosRepository movimientosRepository, ClienteService clienteService, MovimientoMapper movimientosMapper, DomiciliacionRepository domiciliacionRepository, TarjetaService tarjetaService, UserService userService, WebSocketConfig webSocketConfig, NotificationMapper notificationMapper, CuentaMapper cuentaMapper) {
        this.clienteService = clienteService;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientosMapper;
        this.domiciliacionRepository = domiciliacionRepository;
        this.cuentaService = cuentaService;
        this.tarjetaService = tarjetaService;
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
        return movimientosRepository.findByClienteGuid(ClienteGuid)
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
        // validar Iban correcto
        ValidarIban.validateIban(domiciliacion.getIbanOrigen());
        ValidarIban.validateIban(domiciliacion.getIbanDestino());

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

        if (!cliente.getGuid().equals(clienteCuenta.getClienteId())) {
            throw new UnknownIban(domiciliacion.getIbanOrigen());
        }

        // Validar si la domiciliación ya existe
        var clienteDomiciliaciones = domiciliacionRepository.findByClienteGuid(cliente.getGuid());
        if (clienteDomiciliaciones.stream().anyMatch(d -> d.getIbanDestino().equals(domiciliacion.getIbanDestino()))) {
            throw new DuplicatedDomiciliacionException(domiciliacion.getIbanDestino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadDomiciliacion = new BigDecimal(domiciliacion.getCantidad().toString());
        if (cantidadDomiciliacion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadDomiciliacion);
        }

        // Guardar la domiciliación
        domiciliacion.setUltimaEjecucion(LocalDateTime.now()); // Registro inicial
        domiciliacion.setClienteGuid(cliente.getGuid()); // Asigno el id del cliente al domiciliación

        // Notificación al cliente
        onChangeDomiciliacion(Notification.Tipo.CREATE,domiciliacion);

        // Retornar respuesta
        return domiciliacionRepository.save(domiciliacion);
    }

    @Override
    public MovimientoResponse saveIngresoDeNomina(User user, IngresoDeNomina ingresoDeNomina) {
        log.info("Guardando Ingreso de Nomina: {}", ingresoDeNomina);
        // Validar Iban correcto
        ValidarIban.validateIban(ingresoDeNomina.getIban_Destino());
        ValidarIban.validateIban(ingresoDeNomina.getIban_Origen());
        // Validar Cif
        ValidarCif.validateCif(ingresoDeNomina.getCifEmpresa());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(ingresoDeNomina.getIban_Destino());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(ingresoDeNomina.getIban_Destino());
        }

        // Validar que el ingreso de nomina es > 0
        var cantidadNomina = new BigDecimal(ingresoDeNomina.getCantidad().toString());
        if (cantidadNomina.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadNomina);
        }

        // sumar al cliente
        var saldoActual = new BigDecimal(clienteCuenta.getSaldo());
        clienteCuenta.setSaldo(String.valueOf(saldoActual.add(cantidadNomina)));
        cuentaService.update(clienteCuenta.getGuid(), cuentaMapper.toCuentaRequestUpdate(clienteCuenta));

        // Crear el movimiento
        Movimiento movimineto = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .ingresoDeNomina(ingresoDeNomina)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimineto);
        onChangeIngresoNomina(Notification.Tipo.CREATE,ingresoDeNomina);
        return movimientosMapper.toMovimientoResponse(saved);
    }

    @Override
    public MovimientoResponse savePagoConTarjeta(User user, PagoConTarjeta pagoConTarjeta) {
        log.info("Guardando Pago con Tarjeta: {}", pagoConTarjeta);
        // Validar Iban correcto
        ValidarTarjeta.validateTarjeta(pagoConTarjeta.getNumeroTarjeta());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la tarjeta existe
        var clienteTarjeta = tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta());
        if (clienteTarjeta == null) {
            throw new TarjetaNotFoundByNumero(pagoConTarjeta.getNumeroTarjeta());
        }


        // Validar que la cuenta existe
        var clienteCuentas = cuentaService.getAllCuentasByClienteGuid(cliente.getGuid());
        if (clienteCuentas == null) {
            throw new CuentaNotFoundByClienteGuid(cliente.getGuid());
        }

        // Validar que la cuenta asociada a la tarjeta existe
        var cuentaAsociadaATarjeta = clienteCuentas.stream()
                .filter(c -> c.getTarjetaId().equals(clienteTarjeta.getGuid()))
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundByTarjetaId(clienteTarjeta.getGuid()));


        // Validar que la cantidad es mayor que cero
        var cantidadTarjeta = new BigDecimal(pagoConTarjeta.getCantidad().toString());
        if (cantidadTarjeta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTarjeta);
        }

        // Validar saldo suficiente
        BigDecimal saldoActual = new BigDecimal(cuentaAsociadaATarjeta.getSaldo());
        if (saldoActual.compareTo(cantidadTarjeta) < 0) {
            throw new SaldoInsuficienteException(cuentaAsociadaATarjeta.getIban(), saldoActual);
        }

        // restar al cliente
        saldoActual = saldoActual.subtract(cantidadTarjeta);
        cuentaAsociadaATarjeta.setSaldo(String.valueOf(saldoActual));
        cuentaService.update(cuentaAsociadaATarjeta.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaAsociadaATarjeta));

        //crear el movimiento
        Movimiento movimiento = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .pagoConTarjeta(pagoConTarjeta)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimiento);
        onChangePagoConTarjeta(Notification.Tipo.CREATE,pagoConTarjeta);

        return movimientosMapper.toMovimientoResponse(saved);

    }

    @Override
    public MovimientoResponse saveTransferencia(User user, Transferencia transferencia) {
        log.info("Guardando Transferencia: {}", transferencia);

        // Validar Iban correcto
        ValidarIban.validateIban(transferencia.getIban_Destino());
        ValidarIban.validateIban(transferencia.getIban_Origen());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var cuentaOrigen = cuentaService.getByIban(transferencia.getIban_Origen());
        if (cuentaOrigen == null) {
            throw new CuentaNotFound(transferencia.getIban_Origen());
        }

        if (!cliente.getGuid().equals(cuentaOrigen.getClienteId())) {
            throw new UnknownIban(transferencia.getIban_Origen());
        }

        // HACER MOVIMIENTO INVERSO
        var cuentaDestino = cuentaService.getByIban(transferencia.getIban_Destino());
        if (cuentaDestino == null) {
            throw new CuentaNotFound(transferencia.getIban_Destino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadTranseferencia = new BigDecimal(transferencia.getCantidad().toString());
        if (cantidadTranseferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTranseferencia);
        }

        // Validar saldo suficiente
        BigDecimal saldoActual = new BigDecimal(cuentaOrigen.getSaldo());
        if (saldoActual.compareTo(cantidadTranseferencia) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigen.getIban(), saldoActual);
        }

        // restar al cliente
        saldoActual = saldoActual.subtract(cantidadTranseferencia);
        cuentaOrigen.setSaldo(String.valueOf(saldoActual));
        cuentaService.update(cuentaOrigen.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaOrigen));

        // sumar al cliente de la cuenta destino
        var saldoActualDestino = new BigDecimal(cuentaDestino.getSaldo());
        saldoActualDestino = saldoActualDestino.add(cantidadTranseferencia);
        cuentaDestino.setSaldo(String.valueOf(saldoActualDestino));
        cuentaService.update(cuentaDestino.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaDestino));

        // crear el movimiento al cliente destino
        log.info("Crear movimiento destino");
        Movimiento movimientoDestino = Movimiento.builder()
                .clienteGuid(cuentaDestino.getClienteId())
                .transferencia(transferencia)
                .build();

        // Guardar el movimiento destino
        log.info("Guardar movimiento destino");
        movimientosRepository.save(movimientoDestino);

        // crear el movimiento al cliente origen
        Movimiento movimientoOrigen = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .transferencia(Transferencia.builder()
                        .iban_Origen(transferencia.getIban_Origen())
                        .iban_Destino(transferencia.getIban_Destino())
                        .cantidad(transferencia.getCantidad().negate())
                        .nombreBeneficiario(transferencia.getNombreBeneficiario())
                        .movimientoDestino(movimientoDestino.getGuid())
                        .build())
                .build();

        // Guardar el movimiento origen
        var saved = movimientosRepository.save(movimientoOrigen);
        onChangeTransferencia(Notification.Tipo.CREATE,transferencia);

        return movimientosMapper.toMovimientoResponse(saved);
    }

    @Override
    public MovimientoResponse revocarTransferencia(User user, String movimientoTransferenciaGuid) {
        log.info("Revocando Transferencia: {}", movimientoTransferenciaGuid);

        // Obtener el movimiento original
        var movimientoOriginal = movimientosRepository.findByGuid(movimientoTransferenciaGuid)
                .orElseThrow(() -> new MovimientoNotFound(movimientoTransferenciaGuid));

        // validar que no haya pasado 1 dia
        if (!LocalDateTime.now().isBefore(movimientoOriginal.getCreatedAt().plusDays(1))) {
            throw new TransferenciaNoRevocableException(movimientoTransferenciaGuid);
        }

        // Verificar que el movimiento es una transferencia
        if (movimientoOriginal.getTransferencia() == null) {
            throw new MovimientoIsNotTransferenciaException(movimientoTransferenciaGuid);
        }

        // Verificar que el usuario que solicita la revocación es el propietario de la cuenta de origen
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (!cliente.getGuid().equals(movimientoOriginal.getClienteGuid())) {
            throw new UnknownIban(movimientoOriginal.getTransferencia().getIban_Origen());
        }

        // Obtener las cuentas involucradas
        var cuentaOrigen = cuentaService.getByIban(movimientoOriginal.getTransferencia().getIban_Origen());
        var cuentaDestino = cuentaService.getByIban(movimientoOriginal.getTransferencia().getIban_Destino());

        // Revertir la transferencia

        // Restar de la cuenta destino
        BigDecimal cantidadTransferencia = movimientoOriginal.getTransferencia().getCantidad();

        BigDecimal saldoDestino = new BigDecimal(cuentaDestino.getSaldo());
        saldoDestino = saldoDestino.add(cantidadTransferencia);
        cuentaDestino.setSaldo(saldoDestino.toString());
        cuentaService.update(cuentaDestino.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaDestino));

        // Sumar a la cuenta origen
        BigDecimal saldoOrigen = new BigDecimal(cuentaOrigen.getSaldo());
        saldoOrigen = saldoOrigen.subtract(cantidadTransferencia);
        cuentaOrigen.setSaldo(saldoOrigen.toString());
        cuentaService.update(cuentaOrigen.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaOrigen));

        // Marcar el movimiento original como revocado (si es necesario)
        var movimientoOriginalDestino = movimientosRepository.findByGuid(movimientoOriginal.getTransferencia().getMovimientoDestino()).orElseThrow(
                () -> new MovimientoNotFound(movimientoOriginal.getTransferencia().getMovimientoDestino())
        );

        // Marcar ambos movimientos como eliminados
        movimientoOriginal.setIsDeleted(true);
            onChangeTransferencia(Notification.Tipo.DELETE,movimientoOriginal.getTransferencia());
        movimientoOriginalDestino.setIsDeleted(true);
            onChangeTransferencia(Notification.Tipo.DELETE,movimientoOriginalDestino.getTransferencia());
        movimientosRepository.save(movimientoOriginalDestino);
            onChangeTransferencia(Notification.Tipo.CREATE,movimientoOriginalDestino.getTransferencia());
        movimientosRepository.save(movimientoOriginal);
            onChangeTransferencia(Notification.Tipo.CREATE,movimientoOriginal.getTransferencia());


        return movimientosMapper.toMovimientoResponse(movimientoOriginal);
    }

    void onChangeIngresoNomina(Notification.Tipo tipo, IngresoDeNomina data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

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

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    void onChangeTransferencia(Notification.Tipo tipo, Transferencia data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<TransferenciaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toTransferenciaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Notificar tanto al ordenante como al beneficiario

            // Recuperar el cliente del usuario logueado (beneficiario)
            String clienteId = cuentaService.getByIban(data.getIban_Destino()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();
            sendMessageUser(userName, json);

            // Recuperar el cliente del usuario logueado (ordenante)
            clienteId = cuentaService.getByIban(data.getIban_Origen()).getClienteId();
            userId = clienteService.getById(clienteId).getUserId();
            userName = userService.getById(userId).getUsername();
            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }



    void onChangeDomiciliacion(Notification.Tipo tipo, Domiciliacion data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<DomiciliacionResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toDomiciliacionDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            String clienteId = cuentaService.getByIban(data.getIbanOrigen()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    void onChangePagoConTarjeta(Notification.Tipo tipo, PagoConTarjeta data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<PagoConTarjetaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toPagoConTarjetaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            TarjetaResponse tarjeta = tarjetaService.getByNumeroTarjeta(data.getNumeroTarjeta());
            CuentaResponse cuenta =cuentaService.getByNumTarjeta(tarjeta.getNumeroTarjeta());
            String clienteId = cuenta.getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    /**
     * Hace la llamada al método para enviar mensaje al usuario concreto
     * @param userName  Usuario al que se enviará el mensaje
     * @param json      Mensaje a enviar
     * @see WebSocketHandler
     *
     * @author Jaime León, Natalia González,
     *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    private void sendMessageUser(String userName, String json){
        log.info("Enviando mensaje al cliente ws del usuario");
        Thread senderThread = new Thread(() -> {
            try {
                webSocketService.sendMessageToUser(userName,json);
            } catch (Exception e) {
                log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
            }
        });
        senderThread.start();
    }
}
