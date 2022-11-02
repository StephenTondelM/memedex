package com.memecorps.memedex.socketcontroller;

import com.memecorps.memedex.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ChatSocketController {

    @Autowired
    SimpUserRegistry simpUserRegistry;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    @SendTo("/chat/newMessage")
    public ChatMessage broadcastMessage(ChatMessage message, StompHeaderAccessor headers) {
        return message;
    }

    private void sendTo(String destination, Object payload, StompHeaderAccessor headers) {
        Optional<String> user = Optional.ofNullable(headers.getUser())
                .map(Principal::getName);

        if (user.isPresent()) {
            List<String> subscribers = simpUserRegistry.getUsers().stream()
                    .map(SimpUser::getName)
                    .filter(name -> !user.get().equals(name))
                    .collect(Collectors.toList());

            subscribers
                    .forEach(sub -> simpMessagingTemplate.convertAndSendToUser(sub, destination, payload));
        }
    }
}
