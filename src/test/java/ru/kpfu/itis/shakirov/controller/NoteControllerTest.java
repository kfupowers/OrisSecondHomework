package ru.kpfu.itis.shakirov.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.shakirov.model.Note;
import ru.kpfu.itis.shakirov.model.User;
import ru.kpfu.itis.shakirov.repository.NoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteRepository noteRepository;

    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        authentication = new UsernamePasswordAuthenticationToken(testUser, null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void myNotes_shouldReturnNotesViewWithUserNotes() throws Exception {
        Note note1 = new Note();
        note1.setId(1L);
        note1.setTitle("Title 1");
        note1.setContent("Content 1");
        note1.setPublic(true);
        note1.setCreatedAt(LocalDateTime.now());
        note1.setAuthor(testUser);

        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Title 2");
        note2.setContent("Content 2");
        note2.setPublic(false);
        note2.setCreatedAt(LocalDateTime.now());
        note2.setAuthor(testUser);

        List<Note> userNotes = List.of(note1, note2);

        when(noteRepository.findByAuthorOrderByCreatedAtDesc(testUser)).thenReturn(userNotes);

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", userNotes));

        verify(noteRepository).findByAuthorOrderByCreatedAtDesc(testUser);
    }

    @Test
    void publicNotes_shouldReturnPublicNotesView() throws Exception {
        Note note1 = new Note();
        note1.setId(1L);
        note1.setTitle("Title 1");
        note1.setContent("Content 1");
        note1.setPublic(true);
        note1.setAuthor(testUser);
        note1.setCreatedAt(LocalDateTime.now());

        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Title 2");
        note2.setContent("Content 2");
        note2.setPublic(false);
        note2.setAuthor(testUser);
        note2.setCreatedAt(LocalDateTime.now());

        List<Note> publicNotes = List.of(note1, note2);

        when(noteRepository.findByIsPublicTrueOrderByCreatedAtDesc()).thenReturn(publicNotes);

        mockMvc.perform(get("/notes/public"))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", publicNotes));

        verify(noteRepository).findByIsPublicTrueOrderByCreatedAtDesc();
    }

    @Test
    void createForm_shouldReturnNoteFormView() throws Exception {
        mockMvc.perform(get("/notes/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"));
    }

    @Test
    void create_shouldSaveNoteAndRedirect() throws Exception {
        Note noteToCreate = new Note();
        noteToCreate.setTitle("Title");
        noteToCreate.setContent("Content");
        noteToCreate.setPublic(true);

        mockMvc.perform(post("/notes/create")
                        .flashAttr("note", noteToCreate)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void editForm_whenAuthorized_shouldReturnNoteFormView() throws Exception {
        Long noteId = 1L;
        Note note = new Note();
        note.setId(noteId);
        note.setAuthor(testUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/notes/{id}/edit", noteId))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", note));
    }

    @Test
    void editForm_whenUnauthorized_shouldRedirectWithError() throws Exception {
        Long noteId = 1L;
        User otherUser = new User();
        otherUser.setId(2L);
        Note note = new Note();
        note.setAuthor(otherUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        mockMvc.perform(get("/notes/{id}/edit", noteId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes?error=access_denied"));
    }

    @Test
    void update_whenAuthorized_shouldUpdateAndRedirect() throws Exception {
        Long noteId = 1L;
        Note existingNote = new Note();
        existingNote.setId(noteId);
        existingNote.setAuthor(testUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));

        Note updatedNote = new Note();
        updatedNote.setTitle("New Title");
        updatedNote.setContent("New Content");
        updatedNote.setPublic(false);

        mockMvc.perform(post("/notes/{id}/edit", noteId)
                        .flashAttr("note", updatedNote)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteRepository).save(existingNote);
        assert existingNote.getTitle().equals("New Title");
        assert existingNote.getContent().equals("New Content");
        assert !existingNote.isPublic();
    }

    @Test
    void update_whenUnauthorized_shouldRedirectWithError() throws Exception {
        Long noteId = 1L;
        User otherUser = new User();
        otherUser.setId(2L);
        Note existingNote = new Note();
        existingNote.setAuthor(otherUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));

        mockMvc.perform(post("/notes/{id}/edit", noteId)
                        .flashAttr("note", new Note())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes?error=access_denied"));

        verify(noteRepository, never()).save(any());
    }

    @Test
    void delete_whenAuthorized_shouldDeleteAndRedirect() throws Exception {
        Long noteId = 1L;
        Note note = new Note();
        note.setAuthor(testUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        mockMvc.perform(post("/notes/{id}/delete", noteId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteRepository).delete(note);
    }

    @Test
    void delete_whenUnauthorized_shouldRedirectWithError() throws Exception {
        Long noteId = 1L;
        User otherUser = new User();
        otherUser.setId(2L);
        Note note = new Note();
        note.setAuthor(otherUser);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        mockMvc.perform(post("/notes/{id}/delete", noteId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes?error=access_denied"));

        verify(noteRepository, never()).delete(any());
    }
}