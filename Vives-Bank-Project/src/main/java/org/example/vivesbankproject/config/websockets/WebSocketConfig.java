package org.example.vivesbankproject.config.websockets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuración para el soporte de WebSocket.
 *
 * <p>
 * Esta clase es responsable de configurar los endpoints WebSocket para los distintos recursos como movimientos,
 * tarjetas y cuentas. Cada uno de estos recursos tiene su propio manejador asociado.
 * Además, la configuración incluye seguridad mediante interceptores personalizados.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@EnableWebSocket
@Slf4j
@Tag(name = "WebSocketConfig", description = "Configuración para los endpoints WebSocket")
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    /**
     * Registra los endpoints WebSocket para los recursos de movimientos, tarjetas y cuentas.
     * <p>
     * Cada ruta de WebSocket está protegida con un interceptor de seguridad personalizado.
     * El formato de las rutas es dinámico utilizando la versión de la API proporcionada en la configuración.
     * </p>
     *
     * @param registry El registro de handlers WebSocket.
     */
    @Override
    @Operation(summary = "Registrar WebSocket handlers", description = "Configura y registra los endpoints WebSocket con sus respectivos manejadores y seguridad")
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketMovimientosHandler(), "/ws/" + apiVersion + "/movimientos")
                .addInterceptors(new WebSocketSecurityInterceptor());
        registry.addHandler(webSocketTarjetasHandler(), "/ws/" + apiVersion + "/tarjetas")
                .addInterceptors(new WebSocketSecurityInterceptor());
        registry.addHandler(webSocketCuentasHandler(), "/ws/" + apiVersion + "/cuentas")
                .addInterceptors(new WebSocketSecurityInterceptor());

        log.info("WebSocket handlers registrados con éxito");
    }

    /**
     * Crea el handler para los movimientos como un bean de Spring.
     * <p>
     * Este handler gestiona las conexiones WebSocket para el recurso "Movimientos".
     * </p>
     *
     * @return El manejador para el recurso de movimientos.
     */
    @Bean
    @Operation(summary = "Crear handler para Movimientos", description = "Crea el handler para manejar las operaciones de Movimientos en el WebSocket")
    public WebSocketHandler webSocketMovimientosHandler() {
        return new WebSocketHandler("Movimientos");
    }

    /**
     * Crea el handler para las tarjetas como un bean de Spring.
     * <p>
     * Este handler gestiona las conexiones WebSocket para el recurso "Tarjetas".
     * </p>
     *
     * @return El manejador para el recurso de tarjetas.
     */
    @Bean
    @Operation(summary = "Crear handler para Tarjetas", description = "Crea el handler para manejar las operaciones de Tarjetas en el WebSocket")
    public WebSocketHandler webSocketTarjetasHandler() {
        return new WebSocketHandler("Tarjetas");
    }

    /**
     * Crea el handler para las cuentas como un bean de Spring.
     * <p>
     * Este handler gestiona las conexiones WebSocket para el recurso "Cuentas".
     * </p>
     *
     * @return El manejador para el recurso de cuentas.
     */
    @Bean
    @Operation(summary = "Crear handler para Cuentas", description = "Crea el handler para manejar las operaciones de Cuentas en el WebSocket")
    public WebSocketHandler webSocketCuentasHandler() {
        return new WebSocketHandler("Cuentas");
    }
}