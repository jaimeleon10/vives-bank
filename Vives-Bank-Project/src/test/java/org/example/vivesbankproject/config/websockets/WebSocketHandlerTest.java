package org.example.vivesbankproject.config.websockets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSocketHandlerTest {

    private WebSocketHandler webSocketHandler;
    private WebSocketSession sessionMock;
    private final String ENTITY_NAME = "TestEntity";

    @BeforeEach
    void setUp() {
        webSocketHandler = new WebSocketHandler(ENTITY_NAME);
        sessionMock = mock(WebSocketSession.class);
    }

    @Test
    void afterConnectionEstablished() throws Exception {

        when(sessionMock.getAttributes()).thenReturn(Map.of("username", "usuarioTest"));
        when(sessionMock.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(sessionMock);

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Updates Web socket: " + ENTITY_NAME + " - Vives Bank"));

    }

    @Test
    void afterConnectionClosedRemovesUserFromSessions() throws Exception {

        String username = "usuarioTest";
        Map<String, Object> attributes = Map.of("username", username);
        when(sessionMock.getAttributes()).thenReturn(attributes);

        webSocketHandler.afterConnectionEstablished(sessionMock);

        clearInvocations(sessionMock);

        webSocketHandler.afterConnectionClosed(sessionMock, CloseStatus.NORMAL);

        assertDoesNotThrow(() -> webSocketHandler.sendMessageToUser(username, "Test Message"));
        verify(sessionMock, never()).sendMessage(any(TextMessage.class));

    }

    @Test
    void afterConnectionClosedDoesNotThrow() throws Exception {

        String username = "usuarioTest";
        Map<String, Object> attributes = Map.of("username", username);
        when(sessionMock.getAttributes()).thenReturn(attributes);
        when(sessionMock.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(sessionMock);

        clearInvocations(sessionMock);

        assertDoesNotThrow(() -> webSocketHandler.afterConnectionClosed(sessionMock, CloseStatus.NORMAL));

        verify(sessionMock, never()).sendMessage(any(TextMessage.class));

    }

    @Test
    void sendMessageToAllSessions() throws Exception {

        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendMessage("Mensaje genérico");

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Mensaje genérico"));
    }

    @Test
    void sendMessageToSpecificUser() throws Exception {

        String username = "usuarioTest";
        when(sessionMock.getAttributes()).thenReturn(Map.of("username", username));
        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendMessageToUser(username, "Mensaje a usuario especifico");

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Mensaje a usuario especifico"));

    }

    @Test
    void sendPeriodicMessages() throws Exception {

        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendPeriodicMessages();

        verify(sessionMock, atLeastOnce()).sendMessage(any(TextMessage.class));

    }

    @Test
    void handleTransportError() throws Exception {
        webSocketHandler.handleTransportError(sessionMock, new RuntimeException("Test Error"));

        // El método no hace nada. solo asegurarse de que no lanza excepciones
    }

    @Test
    void getSubProtocols() {

        var subProtocols = webSocketHandler.getSubProtocols();

        assertNotNull(subProtocols);
        assertTrue(subProtocols.contains("subprotocol.demo.websocket"));

    }

}
