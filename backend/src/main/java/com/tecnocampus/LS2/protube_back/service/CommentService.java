package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.CommentReaction;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.repository.CommentReactionRepository;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private CommentReactionRepository reactionRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, VideoRepository videoRepository, CommentReactionRepository reactionRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.reactionRepository = reactionRepository;
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

    public String toggleReaction(Long commentId, Long userId, boolean isLike) {

        Optional<CommentReaction> existingReaction = reactionRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingReaction.isPresent()) {
            CommentReaction reaction = existingReaction.get();
            // If the reaction is the same, return a message
            if (reaction.isLike() == isLike) {
                return isLike ? "You already liked this comment." : "You already disliked this comment.";
            }
            // If the reaction is different, switch it
            reaction.setIsLike(isLike);
            reactionRepository.save(reaction);
            return isLike ? "Switched to like." : "Switched to dislike.";
        }

        // Otherwise, create a new reaction
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentReaction reaction = new CommentReaction();
        reaction.setUser(user); // Set the User object
        reaction.setComment(comment); // Set the Comment object
        reaction.setIsLike(isLike);
        reactionRepository.save(reaction);

        return isLike ? "Comment liked." : "Comment disliked.";
    }

    public void updateLikeDislikeCounts(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Count likes and dislikes separately
        int likeCount = reactionRepository.countByCommentIdAndIsLikeTrue(commentId);
        int dislikeCount = reactionRepository.countByCommentIdAndIsLikeFalse(commentId);

        // Update the comment's counters
        comment.setLikes(likeCount);
        comment.setDislikes(dislikeCount);
        commentRepository.save(comment);
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }
}