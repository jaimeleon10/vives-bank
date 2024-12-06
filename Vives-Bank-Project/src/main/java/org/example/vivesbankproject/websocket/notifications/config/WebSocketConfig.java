package org.example.vivesbankproject.websocket.notifications.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${api.version}")
    private String apiVersion;

    // Registra uno por cada tipo de notificación que quieras con su handler y su ruta (endpoint)
    // Cuidado con la ruta que no se repita
    // Para conectar con el cliente, el cliente debe hacer una petición de conexión
    // ws://localhost:3000/ws/v1/productos
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketMovimientosHandler(), "/ws/" + apiVersion + "/movimientos");
        registry.addHandler(webSocketTarjetasHandler(), "/ws/" + apiVersion + "/tarjetas");
        registry.addHandler(webSocketCuentasHandler(), "/ws/" + apiVersion + "/cuentas");
        log.info("WebSocket handlers registrados con éxito");
    }

    // Cada uno de los handlers como bean para que cada vez que nos atienda
    @Bean
    public WebSocketHandler webSocketMovimientosHandler() {
        return new WebSocketHandler("Movimientos");
    }
    @Bean
    public WebSocketHandler webSocketTarjetasHandler() {
        return new WebSocketHandler("Tarjetas");
    }
    @Bean
    public WebSocketHandler webSocketCuentasHandler() {
        return new WebSocketHandler("Cuentas");
    }

}