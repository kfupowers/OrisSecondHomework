package ru.kpfu.itis.shakirov.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.itis.shakirov.model.ChatMessage;
import ru.kpfu.itis.shakirov.model.User;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop50ByOrderBySentAtDesc();

    List<ChatMessage> findByAuthor(User author);

    @Query("SELECT m FROM ChatMessage m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ChatMessage> searchByContent(@Param("keyword") String keyword);

    @Query("SELECT cm FROM ChatMessage cm JOIN FETCH cm.author ORDER BY cm.sentAt DESC")
    List<ChatMessage> findTop50WithAuthor(Pageable pageable);
}