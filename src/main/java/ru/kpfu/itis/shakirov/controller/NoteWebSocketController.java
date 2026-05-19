package ru.kpfu.itis.shakirov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.kpfu.itis.shakirov.dto.NoteNotificationDto;

@Controller
public class NoteWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NoteWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAboutNewNote(NoteNotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/new-note", notification);
    }
}