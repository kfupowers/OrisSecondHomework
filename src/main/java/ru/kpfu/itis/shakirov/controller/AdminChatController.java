package ru.kpfu.itis.shakirov.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.shakirov.model.ChatMessage;
import ru.kpfu.itis.shakirov.repository.ChatMessageRepository;

import java.util.List;

@RestController
@RequestMapping("/admin/messages")
public class AdminChatController {

    private final ChatMessageRepository chatMessageRepository;

    public AdminChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping
    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnyMessage(@PathVariable Long id) {
        chatMessageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}