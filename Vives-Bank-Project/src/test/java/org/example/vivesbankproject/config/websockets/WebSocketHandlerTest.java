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
    void testAfterConnectionEstablished() throws Exception {
        when(sessionMock.getAttributes()).thenReturn(Map.of("username", "testUser"));
        when(sessionMock.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(sessionMock);

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Updates Web socket: " + ENTITY_NAME + " - Vives Bank"));
    }

    @Test
    void testAfterConnectionClosedRemovesUserFromSessions() throws Exception {
        // Arrange
        String username = "testUser";
        Map<String, Object> attributes = Map.of("username", username);
        when(sessionMock.getAttributes()).thenReturn(attributes);

        // Simula el establecimiento de la conexión
        webSocketHandler.afterConnectionEstablished(sessionMock);

        // Limpia las interacciones previas para enfocarnos en lo que pasa después
        clearInvocations(sessionMock);

        // Act: Cierra la conexión
        webSocketHandler.afterConnectionClosed(sessionMock, CloseStatus.NORMAL);

        // Assert
        // Verificar que el usuario fue eliminado del mapa (no hay llamada adicional para el usuario)
        assertDoesNotThrow(() -> webSocketHandler.sendMessageToUser(username, "Test Message"));
        verify(sessionMock, never()).sendMessage(any(TextMessage.class)); // No debe enviar mensajes tras cerrar la conexión
    }


    @Test
    void AfterConnectionClosedDoesNotThrow() throws Exception {
        // Arrange
        String username = "testUser";
        Map<String, Object> attributes = Map.of("username", username);
        when(sessionMock.getAttributes()).thenReturn(attributes);
        when(sessionMock.isOpen()).thenReturn(true);

        // Simula que el usuario se conecta
        webSocketHandler.afterConnectionEstablished(sessionMock);

        // Clear interactions para ignorar el mensaje enviado en afterConnectionEstablished
        clearInvocations(sessionMock);

        // Act
        // Intenta cerrar la conexión
        assertDoesNotThrow(() -> webSocketHandler.afterConnectionClosed(sessionMock, CloseStatus.NORMAL));

        // Assert
        // Verificar que no se envían mensajes adicionales al cerrar
        verify(sessionMock, never()).sendMessage(any(TextMessage.class));
    }
    @Test
    void testSendMessageToAllSessions() throws Exception {
        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendMessage("Broadcast Message");

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Broadcast Message"));
    }

    @Test
    void testSendMessageToSpecificUser() throws Exception {
        String username = "testUser";
        when(sessionMock.getAttributes()).thenReturn(Map.of("username", username));
        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendMessageToUser(username, "Personal Message");

        verify(sessionMock, times(1)).sendMessage(new TextMessage("Personal Message"));
    }

    @Test
    void testSendPeriodicMessages() throws Exception {
        when(sessionMock.isOpen()).thenReturn(true);
        webSocketHandler.afterConnectionEstablished(sessionMock);

        webSocketHandler.sendPeriodicMessages();

        verify(sessionMock, atLeastOnce()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleTransportError() throws Exception {
        webSocketHandler.handleTransportError(sessionMock, new RuntimeException("Test Error"));

        // Assert
        // No specific behavior expected; ensure no exceptions thrown
    }

    @Test
    void testGetSubProtocols() {
        var subProtocols = webSocketHandler.getSubProtocols();

        assertNotNull(subProtocols);
        assertTrue(subProtocols.contains("subprotocol.demo.websocket"));
    }
}
