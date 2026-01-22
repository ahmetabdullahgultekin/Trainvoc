package com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.handler;

import org.json.JSONObject;
import org.springframework.web.socket.WebSocketSession;

/**
 * Interface for WebSocket message handlers.
 * Each implementation handles a specific message type.
 */
public interface WebSocketMessageHandler {

    /**
     * Returns the message type this handler processes.
     */
    String getMessageType();

    /**
     * Handles the incoming message.
     *
     * @param session The WebSocket session
     * @param message The parsed JSON message
     * @param context Context for accessing shared resources like session registry
     */
    void handle(WebSocketSession session, JSONObject message, WebSocketContext context) throws Exception;
}
