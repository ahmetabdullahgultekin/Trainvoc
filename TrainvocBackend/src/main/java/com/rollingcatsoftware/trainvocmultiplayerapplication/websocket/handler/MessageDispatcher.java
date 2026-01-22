package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatches WebSocket messages to appropriate handlers based on message type.
 * Implements the Strategy pattern for message handling.
 */
@Component
public class MessageDispatcher {

    private final Map<String, WebSocketMessageHandler> handlers = new HashMap<>();
    private final WebSocketContext context = new WebSocketContext();

    public MessageDispatcher(List<WebSocketMessageHandler> messageHandlers) {
        for (WebSocketMessageHandler handler : messageHandlers) {
            handlers.put(handler.getMessageType(), handler);
        }
    }

    /**
     * Dispatches a message to the appropriate handler.
     *
     * @param session The WebSocket session
     * @param message The raw message payload
     * @throws Exception if message handling fails
     */
    public void dispatch(WebSocketSession session, String message) throws Exception {
        JSONObject json = new JSONObject(message);
        String type = json.optString("type", "");

        WebSocketMessageHandler handler = handlers.get(type);
        if (handler != null) {
            handler.handle(session, json, context);
        } else {
            sendUnknownTypeError(session, type);
        }
    }

    /**
     * Gets the WebSocket context for session management.
     */
    public WebSocketContext getContext() {
        return context;
    }

    private void sendUnknownTypeError(WebSocketSession session, String type) throws Exception {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", "Unknown message type: " + type);
        context.sendMessage(session, error);
    }
}
