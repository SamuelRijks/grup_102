package com.tecnocampus.LS2.protube_back.dto;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {
    private Long id;       // Identificador del comentari
    private String content; // Text del comentari
    private Long videoId;  // Identificador del vídeo al que pertany el comentari
    private String author;  // Nom de l'autor
    private Long userId;    // Identificador de l'autor
    private LocalDateTime timestamp; // Marca de temps del comentari
    private String videoTitle; // Títol del vídeo al que pertany el comentari
    private int likes;      // Número de "M'agrada"
    private int dislikes;   // Número de "No m'agrada"
    private boolean userLiked; // Whether the current user liked the comment
    private boolean userDisliked;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor().getUsername();
        this.userId = comment.getAuthor().getId();
        this.timestamp = comment.getTimestamp();
        this.likes = comment.getLikes();
        this.dislikes = comment.getDislikes();
        this.videoId = comment.getVideo().getId();
        this.videoTitle = comment.getVideo().getTitle();
        this.userLiked = false;
        this.userDisliked = false;
    }
}
