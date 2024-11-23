package com.tecnocampus.LS2.protube_back.domain;

import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private int likes;
    private int dislikes;

    public CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor().getUsername()); // Suposant que User té `getUsername()`
        dto.setTimestamp(comment.getTimestamp());
        dto.setLikes(comment.getLikes());
        dto.setDislikes(comment.getDislikes());
        return dto;
    }
}