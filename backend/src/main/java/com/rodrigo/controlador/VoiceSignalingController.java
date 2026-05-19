package com.rodrigo.controlador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.HashMap;

//Clase para las llamadas de voz(No entiendo un nada aun)

@RestController
public class VoiceSignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/voice/join")
    public void join(@Payload Map<String, Object> payload) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "USER_JOINED");
        event.put("userId", payload.get("userId"));

        messagingTemplate.convertAndSend(
            "/topic/voice/" + payload.get("channelId"), event
        );
    }

    @MessageMapping("/voice/leave")
    public void leave(@Payload Map<String, Object> payload) {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "USER_LEFT");
        event.put("userId", payload.get("userId"));

        messagingTemplate.convertAndSend(
            "/topic/voice/" + payload.get("channelId"), event
        );
    }

    @MessageMapping("/voice/signal")
    public void signal(@Payload Map<String, Object> payload) {
        String targetUserId = payload.get("targetUserId").toString();
        // Reenviar la señal WebRTC al usuario destino
        messagingTemplate.convertAndSend(
            "/topic/voice/signal/" + targetUserId, payload
        );
    }
}
