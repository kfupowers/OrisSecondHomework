package ru.kpfu.itis.shakirov.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.kpfu.itis.shakirov.model.ChatMessage;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.ChatMessageRepository;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatMessageHandler {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageHandler(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage handleMessage(ChatMessage message, Principal principal) {
        User currentUser = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        message.setAuthor(currentUser);
        message.setSentAt(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }
}