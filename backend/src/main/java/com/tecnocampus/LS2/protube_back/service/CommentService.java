package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public Comment createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();

        User user = userRepository.findByUsername(commentDTO.getAuthor()).orElseThrow(() -> new RuntimeException("User not found"));
        Video video = videoRepository.findById(commentDTO.getVideoId()).orElseThrow(() -> new RuntimeException("Video not found"));

        comment.setAuthor(user);
        comment.setVideo(video);
        comment.setContent(commentDTO.getContent());
        comment.setTimestamp(commentDTO.getTimestamp());

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this comment");
        }
        commentRepository.delete(comment);
    }

    public Comment updateComment(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this comment");
        }
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        // Logic to check if the user has already liked the comment can be added here
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }

    public void dislikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        // Logic to check if the user has already disliked the comment can be added here
        comment.setDislikes(comment.getDislikes() + 1);
        commentRepository.save(comment);
    }
}