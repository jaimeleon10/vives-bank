package org.example.vivesbankproject.websocket.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender{

    private final String entity;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // Este mapa almacena la relación entre el nombre de usuario y su sesión de WebSocket.
    private final Map<String, WebSocketSession> userSessionsMap = new ConcurrentHashMap<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión establecida con el servidor: " + session);
        log.info("Sesión: " + session);

        // obtenemos el nombre del usuario autenticado y asociamos la sesión de WebSocket
        // con el nombre de usuario en userSessionsMap
        //String username = getUsername();

        // Recuperar el nombre de usuario desde los atributos de la sesión
        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            userSessionsMap.put(username, session);
            log.info("Usuario: " + username + " añadido a mapa de sesiones");
        }

        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - Vives Bank");
        log.info("Servidor envía: {}", message);
        session.sendMessage(message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Cerrando conexión con el servidor: " + status);

        // Eliminar sesión del usuario del mapa de sesiones
        String username = (String) session.getAttributes().get("username");
        //String username = getUsername();
        if (username != null) {
            userSessionsMap.remove(username);
        }
        sessions.remove(session);
        log.info("Conexión cerrada con el servidor: " + status);
    }

    // este método es para enviar mensajes a todas las sesiones
    @Override
    public void sendMessage(String message) throws IOException {
        log.info("Enviar mensaje de cambios en la entidad: " + entity + " : " + message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                log.info("Servidor WS envía: " + message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Override
    // envía mensajes a un usuario específico basado en su nombre de usuario
    public void sendMessageToUser(String username, String message) throws IOException {
        log.info("Enviar mensaje de cambios en la entidad: " + entity + " a usuario: " + username + " : " + message);

        WebSocketSession session = userSessionsMap.get(username);
        if (session != null && session.isOpen()) {
            log.info("Servidor WS envía a " + username + " : " + message);
            session.sendMessage(new TextMessage(message));
        } else {
            log.info("Usuario: " + username + " no conectado, no se le envió cambios en la entidad: " + entity);

        }
    }

    @Scheduled(fixedRate = 1000)
    public void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                //log.info("Server sends: " + broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // No hacer nada con los mensajes recibidos
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: " + exception.getMessage());
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
        log.info("getUsername: " + principal.toString());
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }

    }
}
