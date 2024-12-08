package org.example.vivesbankproject.rest.movimientos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.DomiciliacionException;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.rest.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.example.vivesbankproject.rest.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.users.services.UserService;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.config.websockets.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.dto.DomiciliacionResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/**
 * DomiciliacionScheduler
 *
 * <p>Servicio para procesar domiciliaciones periódicas, validar saldos, enviar notificaciones
 * a clientes a través de WebSocket y actualizar la base de datos con la información correspondiente.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
@EnableScheduling
public class DomiciliacionScheduler {

    private final DomiciliacionRepository domiciliacionRepository;
    private final MovimientosRepository movimientosRepository;
    private final CuentaService cuentaService;
    private final CuentaMapper cuentaMapper;
    private final UserService userService;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final NotificationMapper notificationMapper;
    private final ClienteService clienteService;
    /**
     * WebSocket handler para enviar notificaciones a los clientes.
     */
    @Setter
    private WebSocketHandler webSocketService;
    /**
     * Constructor para la inicialización de dependencias necesarias para el servicio.
     *
     * @param domiciliacionRepository El repositorio de domiciliaciones.
     * @param movimientosRepository  El repositorio de movimientos.
     * @param cuentaService         El servicio de cuentas.
     * @param userService           El servicio de usuarios.
     * @param webSocketConfig       La configuración para WebSocket.
     * @param notificationMapper    El mapper para convertir objetos de notificación.
     * @param cuentaMapper          El mapper para convertir las cuentas.
     * @param clienteService       El servicio para obtener clientes.
     * @param movimientosMapper    El mapper para manejar movimientos.
     */
    @Autowired
    public DomiciliacionScheduler(DomiciliacionRepository domiciliacionRepository, MovimientosRepository movimientosRepository, CuentaService cuentaService, UserService userService, WebSocketConfig webSocketConfig, NotificationMapper notificationMapper, CuentaMapper cuentaMapper, ClienteService clienteService, MovimientoMapper movimientosMapper) {
        this.domiciliacionRepository = domiciliacionRepository;
        this.movimientosRepository = movimientosRepository;
        this.cuentaService = cuentaService;
        this.cuentaMapper = cuentaMapper;
        this.clienteService = clienteService;
        this.userService = userService;
        this.webSocketConfig = webSocketConfig;

        webSocketService = webSocketConfig.webSocketMovimientosHandler();
        mapper = new ObjectMapper();
        this.notificationMapper = notificationMapper;
    }
    /**
     * Programa de ejecución periódica cada minuto para procesar domiciliaciones activas.
     */
    @Scheduled(cron = "0 * * * * ?")
    @Operation(
            summary = "Procesar domiciliaciones periódicas",
            description = "Ejecuta domiciliaciones activas verificando su periodicidad y saldo disponible."
    )
    public void procesarDomiciliaciones() {
        log.info("Procesando domiciliaciones periódicas");

        LocalDateTime ahora = LocalDateTime.now();

        // Filtrar domiciliaciones activas que requieren ejecución
        List<Domiciliacion> domiciliaciones = domiciliacionRepository.findAll()
                .stream()
                .filter(d -> d.getActiva() && requiereEjecucion(d, ahora))
                .toList();

        for (Domiciliacion domiciliacion : domiciliaciones) {
            try {
                // Validar y ejecutar la domiciliación
                log.info("Ejecutando domiciliación: {}", domiciliacion.getGuid());
                ejecutarDomiciliacion(domiciliacion);

                // Actualizar última ejecución
                domiciliacion.setUltimaEjecucion(ahora);
                domiciliacionRepository.save(domiciliacion);
            } catch (SaldoInsuficienteException ex) {
                log.warn("Saldo insuficiente para domiciliación: {}", domiciliacion.getGuid());
            } catch (DomiciliacionException ex) {
                log.error("Error al procesar domiciliación: {}", domiciliacion.getGuid(), ex);
            }
        }
    }
    /**
     * Verifica la necesidad de ejecutar una domiciliación según la periodicidad configurada.
     *
     * @param domiciliacion El objeto de domiciliación.
     * @param ahora         Fecha y hora actual para comparar.
     * @return true si debe ejecutarse; false de lo contrario.
     */
    private boolean requiereEjecucion(Domiciliacion domiciliacion, LocalDateTime ahora) {
        switch (domiciliacion.getPeriodicidad()) {
            case DIARIA:
                return domiciliacion.getUltimaEjecucion().plusDays(1).isBefore(ahora);
            case SEMANAL:
                return domiciliacion.getUltimaEjecucion().plusWeeks(1).isBefore(ahora);
            case MENSUAL:
                return domiciliacion.getUltimaEjecucion().plusMonths(1).isBefore(ahora);
            case ANUAL:
                return domiciliacion.getUltimaEjecucion().plusYears(1).isBefore(ahora);
            default:
                return false;
        }
    }

    /**
     * Lógica para ejecutar una domiciliación específica.
     *
     * @param domiciliacion Domiciliación a ejecutar.
     */
    @Operation(summary = "Ejecutar domiciliación lógica", description = "Procesa una domiciliación verificando saldo y registrando los movimientos.")
    private void ejecutarDomiciliacion(Domiciliacion domiciliacion) {
        var cuentaOrigen = cuentaService.getByIban(domiciliacion.getIbanOrigen());

        BigDecimal saldoActual = new BigDecimal(cuentaOrigen.getSaldo());
        BigDecimal cantidad = new BigDecimal(String.valueOf(domiciliacion.getCantidad()));

        // Validar saldo suficiente
        if (saldoActual.compareTo(cantidad) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigen.getIban(), saldoActual);
        }

        // Actualizar saldos
        cuentaOrigen.setSaldo(saldoActual.subtract(cantidad).toString());

        cuentaService.update(cuentaOrigen.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaOrigen));

        // Registrar el movimiento
        Movimiento movimiento = Movimiento.builder()
                .clienteGuid(cuentaOrigen.getClienteId())
                .domiciliacion(domiciliacion)
                .build();

        movimientosRepository.save(movimiento);
        onChangeDomiciliacionEjecutada(Notification.Tipo.EXECUTE, domiciliacion);

    }
    /**
     * Envía una notificación utilizando WebSocket cuando una domiciliación es ejecutada.
     *
     * @param tipo Tipo de notificación a enviar.
     * @param data Información sobre la domiciliación ejecutada.
     */
    @Operation(
            summary = "Enviar notificación al cliente WS",
            description = "Envía una notificación al cliente correspondiente utilizando WebSocket tras ejecutar una domiciliación."
    )
    void onChangeDomiciliacionEjecutada(Notification.Tipo tipo, Domiciliacion data) {
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
    /**
     * Envía un mensaje al cliente vía WebSocket utilizando el nombre de usuario.
     *
     * @param userName Nombre de usuario al que se enviará el mensaje.
     * @param json     Contenido de la notificación en formato JSON.
     */
    @Operation(
            summary = "Enviar mensaje WS al usuario",
            description = "Envía un mensaje vía WebSocket a un usuario específico con la información proporcionada."
    )
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
