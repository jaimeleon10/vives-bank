package org.example.vivesbankproject.websocket.notifications.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

/**
 * Interfaz para enviar mensajes por WebSockets.
 *
 * <p>
 * Esta interfaz define los métodos necesarios para enviar mensajes a través de una conexión WebSocket.
 * Permite enviar mensajes a todos los clientes conectados, a usuarios específicos y realizar envíos
 * periódicos.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Tag(name = "WebSocketSender", description = "Interfaz para enviar mensajes a través de WebSocket")
public interface WebSocketSender {

    /**
     * Envía un mensaje a todos los clientes conectados por WebSocket.
     *
     * <p>
     * Envía un mensaje de tipo broadcast a todas las sesiones WebSocket abiertas.
     * </p>
     *
     * @param message El mensaje que se enviará a todos los clientes.
     * @throws IOException En caso de error en la transmisión del mensaje.
     */
    @Operation(summary = "Enviar mensaje a todos los clientes", description = "Envía un mensaje de tipo broadcast a todos los clientes WebSocket conectados")
    void sendMessage(String message) throws IOException;

    /**
     * Envía un mensaje a un usuario específico a través de su sesión WebSocket.
     *
     * <p>
     * Busca la sesión asociada al nombre de usuario proporcionado y le envía un mensaje si la sesión está activa.
     * </p>
     *
     * @param username El nombre del usuario al que se enviará el mensaje.
     * @param message El mensaje que se enviará al usuario específico.
     * @throws IOException En caso de error en la transmisión del mensaje.
     */
    @Operation(summary = "Enviar mensaje a un usuario específico", description = "Envía un mensaje solo al usuario identificado a través de su sesión WebSocket")
    void sendMessageToUser(String username, String message) throws IOException;

    /**
     * Envía mensajes periódicos a todos los clientes conectados en un intervalo de tiempo regular.
     *
     * <p>
     * Este método está diseñado para enviar mensajes automáticamente en intervalos programados a través
     * de la lógica de envío periódica, como notificaciones automáticas.
     * </p>
     *
     * @throws IOException En caso de error en la transmisión de los mensajes periódicos.
     */
    @Operation(summary = "Enviar mensajes periódicos", description = "Envía mensajes periódicamente a todos los clientes WebSocket conectados en intervalos regulares")
    void sendPeriodicMessages() throws IOException;
}