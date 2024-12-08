package org.example.vivesbankproject.config.websockets;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Interceptor para manejar la autenticación durante el proceso de conexión WebSocket.
 *
 * <p>
 * Esta clase implementa {@link HandshakeInterceptor} para verificar la autenticación de los usuarios
 * durante el intento de establecer una conexión WebSocket. Si el usuario está autenticado, su nombre
 * de usuario se almacena en los atributos de la sesión para ser utilizado posteriormente.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Tag(name = "WebSocketSecurityInterceptor", description = "Interceptor para asegurar las conexiones WebSocket basadas en autenticación")
public class WebSocketSecurityInterceptor implements HandshakeInterceptor {

    /**
     * Se ejecuta antes de establecer la conexión WebSocket.
     *
     * <p>
     * Comprueba si el usuario está autenticado. Si lo está, almacena el nombre de usuario en los atributos
     * de la sesión WebSocket para futuras referencias. Si no lo está, la conexión es rechazada.
     * </p>
     *
     * @param request   La solicitud HTTP que está intentando establecer la conexión.
     * @param response  La respuesta HTTP para enviar la información necesaria.
     * @param wsHandler El manejador de la conexión WebSocket.
     * @param attributes Un mapa para almacenar atributos de la conexión WebSocket.
     * @return {@code true} si la conexión debe proceder; {@code false} de lo contrario.
     * @throws Exception En caso de error en el proceso de autenticación.
     */
    @Override
    @Operation(summary = "Antes de establecer la conexión", description = "Verifica la autenticación de los usuarios antes de permitir una conexión WebSocket")
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

    /**
     * Se ejecuta después de que la conexión se ha establecido (handshake).
     *
     * <p>
     * Este método puede realizar acciones adicionales después de que la conexión se ha establecido,
     * pero en este caso, no es necesario implementar ninguna lógica.
     * </p>
     *
     * @param request   La solicitud HTTP.
     * @param response  La respuesta HTTP.
     * @param wsHandler El manejador de la sesión WebSocket.
     * @param exception Excepción lanzada durante el proceso de establecimiento, si la hubiera.
     */
    @Override
    @Operation(summary = "Después de establecer la conexión", description = "Este método se ejecuta después de establecer la conexión, actualmente no realiza acciones")
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // No se realiza ninguna lógica posterior
    }
}