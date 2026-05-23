package ru.kpfu.itis.shakirov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.shakirov.model.ChatMessage;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.ChatMessageRepository;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;

    public ChatController(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping
    public String chatPage(@AuthenticationPrincipal User currentUser, Model model) {
        List<ChatMessage> messages = chatMessageRepository.findTop50ByOrderBySentAtDesc();
        java.util.Collections.reverse(messages);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", currentUser.getUsername());
        return "chat";
    }

    @GetMapping("/public")
    public String publicHistory(Model model) {
        List<ChatMessage> messages = chatMessageRepository.findTop50ByOrderBySentAtDesc();
        java.util.Collections.reverse(messages);
        model.addAttribute("messages", messages);
        return "public_chat";
    }

    @GetMapping("/my")
    public String myMessages(@AuthenticationPrincipal User currentUser, Model model) {
        List<ChatMessage> messages = chatMessageRepository.findByAuthor(currentUser);
        model.addAttribute("messages", messages);
        return "my_messages";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessage(@PathVariable Long id,
                                @AuthenticationPrincipal User currentUser) {
        chatMessageRepository.findById(id).ifPresent(msg -> {
            if (msg.getAuthor().getId().equals(currentUser.getId())) {
                chatMessageRepository.delete(msg);
            }
        });
        return "redirect:/chat/my";
    }
}