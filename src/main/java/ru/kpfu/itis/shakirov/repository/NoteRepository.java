package ru.kpfu.itis.shakirov.repository;

import ru.kpfu.itis.shakirov.model.Note;
import ru.kpfu.itis.shakirov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByAuthorOrderByCreatedAtDesc(User author);

    List<Note> findByIsPublicTrueOrderByCreatedAtDesc();

    @Query("SELECT n FROM Note n WHERE n.author = :author AND LOWER(n.title) LIKE LOWER(CONCAT('%', :titlePart, '%'))")
    List<Note> findByAuthorAndTitleContainingIgnoreCase(@Param("author") User author, @Param("titlePart") String titlePart);

}