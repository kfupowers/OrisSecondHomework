package ru.kpfu.itis.shakirov.controller;

import ru.kpfu.itis.shakirov.model.Note;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String myNotes(@AuthenticationPrincipal User currentUser, Model model) {
        List<Note> notes = noteRepository.findByAuthorOrderByCreatedAtDesc(currentUser);
        model.addAttribute("notes", notes);
        return "notes";
    }

    @GetMapping("/public")
    public String publicNotes(Model model) {
        List<Note> publicNotes = noteRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        model.addAttribute("notes", publicNotes);
        return "public_notes";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("note", new Note());
        return "note_form";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal User currentUser,
                         @ModelAttribute Note note) {
        note.setAuthor(currentUser);
        note.setCreatedAt(LocalDateTime.now());
        note.setPublic(note.isPublic());
        noteRepository.save(note);
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id,
                           @AuthenticationPrincipal User currentUser,
                           Model model) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        if (!note.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/notes?error=access_denied";
        }
        model.addAttribute("note", note);
        return "note_form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @AuthenticationPrincipal User currentUser,
                         @ModelAttribute Note updatedNote) {
        Note existing = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        if (!existing.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/notes?error=access_denied";
        }
        existing.setTitle(updatedNote.getTitle());
        existing.setContent(updatedNote.getContent());
        existing.setPublic(updatedNote.isPublic());
        noteRepository.save(existing);
        return "redirect:/notes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @AuthenticationPrincipal User currentUser) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note not found"));
        if (!note.getAuthor().getId().equals(currentUser.getId())) {
            return "redirect:/notes?error=access_denied";
        }
        noteRepository.delete(note);
        return "redirect:/notes";
    }
}