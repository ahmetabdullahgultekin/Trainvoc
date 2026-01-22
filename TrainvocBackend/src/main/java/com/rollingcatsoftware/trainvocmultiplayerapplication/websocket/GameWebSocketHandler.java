package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket;

import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler.MessageDispatcher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Main WebSocket handler that delegates message processing to the MessageDispatcher.
 * Follows the Single Responsibility Principle by focusing only on WebSocket lifecycle.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final MessageDispatcher messageDispatcher;

    public GameWebSocketHandler(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        messageDispatcher.dispatch(session, message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        // Connection established - could log or track active connections
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        // Connection closed - cleanup could be handled here
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        // Handle transport errors - could log or notify
    }
}
