package org.example.vivesbankproject.websocket.notifications.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Clase encargada de manejar la lógica de las sesiones WebSocket.
 *
 * <p>
 * Esta clase extiende {@link TextWebSocketHandler} para implementar un servidor WebSocket
 * que se encarga de gestionar las sesiones activas de WebSocket, enviar mensajes periódicos,
 * enviar mensajes a todos los usuarios o a usuarios específicos y gestionar los eventos de conexión
 * y desconexión.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Slf4j
@Component
@Tag(name = "WebSocketHandler", description = "Clase para manejar la lógica de conexión y envío de mensajes mediante WebSocket")
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {

    private final String entity;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // Este mapa almacena la relación entre el nombre de usuario y su sesión de WebSocket.
    private final Map<String, WebSocketSession> userSessionsMap = new ConcurrentHashMap<>();

    /**
     * Constructor principal para establecer la entidad a la que está asociado este handler.
     *
     * @param entity Nombre de la entidad relacionada con el handler.
     */
    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    /**
     * Se ejecuta después de que una conexión WebSocket se haya establecido correctamente.
     *
     * @param session La sesión de WebSocket establecida.
     */
    @Override
    @Operation(summary = "Conexión establecida", description = "Maneja la lógica después de establecer una nueva conexión WebSocket")
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión establecida con el servidor: " + session);

        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            userSessionsMap.put(username, session);
            log.info("Usuario: {} añadido al mapa de sesiones", username);
        }

        sessions.add(session);
        TextMessage message = new TextMessage("Actualizaciones WebSocket: " + entity + " - Vives Bank");
        log.info("Mensaje enviado: {}", message);
        session.sendMessage(message);
    }

    /**
     * Se ejecuta después de cerrar una conexión WebSocket.
     *
     * @param session La sesión que se cierra.
     * @param status El estado de cierre de la conexión.
     */
    @Override
    @Operation(summary = "Cerrar conexión", description = "Lógica al cerrar una conexión WebSocket")
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Cerrando la conexión: {}", status);

        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            userSessionsMap.remove(username);
            log.info("Usuario: {} eliminado del mapa de sesiones", username);
        }

        sessions.remove(session);
        log.info("Sesión cerrada");
    }

    /**
     * Envía un mensaje a todas las sesiones activas.
     *
     * @param message Mensaje que se enviará a todas las sesiones activas.
     */
    @Override
    @Operation(summary = "Enviar mensaje a todas las sesiones", description = "Envía un mensaje a todas las sesiones activas")
    public void sendMessage(String message) throws IOException {
        log.info("Enviando mensaje a todas las sesiones activas: {}", message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                log.info("Mensaje enviado: {}", message);
            }
        }
    }

    /**
     * Envía un mensaje a un usuario específico basado en su nombre de usuario.
     *
     * @param username Nombre de usuario del destinatario.
     * @param message Mensaje que se enviará.
     */
    @Override
    @Operation(summary = "Enviar mensaje a usuario específico", description = "Envía un mensaje a una sesión específica de usuario")
    public void sendMessageToUser(String username, String message) throws IOException {
        log.info("Intentando enviar mensaje a {}: {}", username, message);

        WebSocketSession session = userSessionsMap.get(username);

        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
            log.info("Mensaje enviado a usuario {}: {}", username, message);
        } else {
            log.warn("Usuario {} no está conectado, no se enviará el mensaje", username);
        }
    }

    /**
     * Envía mensajes periódicos a todas las sesiones activas cada segundo.
     */
    @Scheduled(fixedRate = 1000)
    @Operation(summary = "Enviar mensajes periódicos", description = "Envía un mensaje periódico a todas las sesiones cada segundo")
    public void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "Mensaje periódico del servidor: " + LocalTime.now();
                session.sendMessage(new TextMessage(broadcast));
                log.info("Mensaje periódico enviado: {}", broadcast);
            }
        }
    }

    @Override
    @Operation(summary = "Mensajes recibidos", description = "Lógica para procesar mensajes recibidos (actualmente vacía)")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Mensaje recibido: {}", message.getPayload());
    }

    @Override
    @Operation(summary = "Manejo de errores", description = "Maneja los errores durante la comunicación WebSocket")
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error en la conexión WebSocket: {}", exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return List.of("subprotocol.demo.websocket");
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("No authentication found");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}