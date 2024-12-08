package org.example.vivesbankproject.rest.movimientos.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    // Para los test
    @Setter
    private WebSocketHandler webSocketService;

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

    @Scheduled(cron = "0 * * * * ?") // Ejecución diaria a medianoche
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
