package com.rollingcatsoftware.trainvocmultiplayerapplication.config;

import com.rollingcatsoftware.trainvocmultiplayerapplication.websocket.GameWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameWebSocketHandler handler;

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000,https://trainvoc.rollingcatsoftware.com}")
    private String allowedOrigins;

    public WebSocketConfig(GameWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/game")
                .setAllowedOrigins(allowedOrigins.split(","));
    }
}