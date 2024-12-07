package org.example.vivesbankproject.websocket.notifications.config;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketSecurityInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // Obtener la autenticación del contexto de seguridad actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            // Almacenar el nombre de usuario en los atributos de la sesión de WebSocket
            attributes.put("username", authentication.getName());
            return true; // Permitir la conexión WebSocket
        }

        // Si no hay autenticación, rechazar la conexión
        return false;

    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Este método se puede dejar vacío si no se necesita realizar ninguna acción post-handshake

    }
}
