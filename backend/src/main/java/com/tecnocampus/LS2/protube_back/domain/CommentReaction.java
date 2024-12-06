package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comment_reactions")
@Getter
@Setter
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;


    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    // Constructors, Getters, and Setters
    public CommentReaction() {
    }

    public CommentReaction(User user, Comment comment, boolean isLike) {
        this.user = user;
        this.comment = comment;
        this.isLike = isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }
}
