package ru.kpfu.itis.shakirov.dto;

import java.time.LocalDateTime;

public class NoteNotificationDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;

    public NoteNotificationDto() {
    }

    public NoteNotificationDto(Long id, String title, String author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}