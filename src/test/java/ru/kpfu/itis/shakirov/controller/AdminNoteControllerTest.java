package ru.kpfu.itis.shakirov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.shakirov.model.Note;
import ru.kpfu.itis.shakirov.repository.NoteRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminNoteController.class)
@WithMockUser(roles = "ADMIN")
class AdminNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteRepository noteRepository;

    @Test
    void getAllNotes_shouldReturnNotesViewWithNotes() throws Exception {
        Note note1 = new Note();
        note1.setId(1L);
        note1.setTitle("Title 1");
        note1.setContent("Content 1");
        note1.setPublic(true);
        note1.setCreatedAt(LocalDateTime.now());
        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Title 2");
        note2.setContent("Content 2");
        note2.setPublic(false);
        note2.setCreatedAt(LocalDateTime.now());
        List<Note> mockNotes = List.of(note1, note2);
        when(noteRepository.findAll()).thenReturn(mockNotes);

        mockMvc.perform(get("/admin/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", mockNotes));
    }

    @Test
    void deleteNote_shouldDeleteAndReturnNotesViewWithUpdatedNotes() throws Exception {
        Long noteId = 1L;
        Note note1 = new Note();
        note1.setId(1L);
        note1.setTitle("Title 1");
        note1.setContent("Content 1");
        note1.setPublic(true);
        note1.setCreatedAt(LocalDateTime.now());
        List<Note> notesAfterDeletion = List.of(note1);
        doNothing().when(noteRepository).deleteById(noteId);
        when(noteRepository.findAll()).thenReturn(notesAfterDeletion);

        mockMvc.perform(delete("/admin/notes/{id}", noteId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", notesAfterDeletion));

        verify(noteRepository).deleteById(noteId);
        verify(noteRepository).findAll();
    }
}