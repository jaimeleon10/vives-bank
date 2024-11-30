package org.example.vivesbankproject.websocket.notifications.config;

import java.io.IOException;

/**
 * Interfaz para enviar mensajes por WebSockets
 */
public interface WebSocketSender {

    void sendMessage(String message) throws IOException;

    void sendMessageToUser(String username, String message) throws IOException;

    void sendPeriodicMessages() throws IOException;
}