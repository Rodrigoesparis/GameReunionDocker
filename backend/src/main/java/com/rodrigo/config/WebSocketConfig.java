package com.rodrigo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override //Como se entura el mensaje
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); //Canal de los mensajes
        config.setApplicationDestinationPrefixes("/app"); //Prefijo para enviar mensajes desde el cliente al servidor
    }

    @Override
public void registerStompEndpoints(StompEndpointRegistry registry) {  //Punto de conexion
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS(); //Fallback para navegadores que no soportan WebSocket
}
}
