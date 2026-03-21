package ru.kpfu.itis.shakirov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.kpfu.itis.shakirov.model.Note;
import ru.kpfu.itis.shakirov.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/notes")
public class AdminNoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public String getAllNotes(Model model) {
        List<Note> notes = noteRepository.findAll();
        model.addAttribute("notes", notes);
        return "notes";
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable("id") Long id, Model model) {
        noteRepository.deleteById(id);
        List<Note> notes = noteRepository.findAll();
        model.addAttribute("notes", notes);
        return "notes";
    }
}