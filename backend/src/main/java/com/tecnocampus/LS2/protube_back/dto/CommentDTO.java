package com.tecnocampus.LS2.protube_back.dto;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private String content; // Text del comentari
    private String author;  // Nom de l'autor
    private LocalDateTime timestamp; // Marca de temps del comentari
    private int likes;      // Número de "M'agrada"
    private int dislikes;   // Número de "No m'agrada"

public CommentDTO(Comment comment) {
    this.content = comment.getContent();
    this.author = comment.getAuthor().getUsername();
    this.timestamp = comment.getTimestamp();
    this.likes = comment.getLikes();
    this.dislikes = comment.getDislikes();
}

    public CommentDTO() {
    }
}

