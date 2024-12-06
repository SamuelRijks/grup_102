package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        try {
            Optional<Comment> commentOpt = commentService.findById(commentId);
            if (commentOpt.isPresent()) {
                CommentDTO commentDTO = new CommentDTO(commentOpt.get());
                return ResponseEntity.ok(commentDTO);
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Comment not found"));
            }
        } catch (Exception e) { 
            // Log the exception for debugging purposes
            System.err.println("Error fetching comment with ID " + commentId + ": " + e.getMessage());
            e.printStackTrace();

            // Return a 500 status code with an error message
            return ResponseEntity.status(500).body(Map.of("error", "An error occurred while fetching the comment"));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDTO) {
        System.out.println("Author " + commentDTO.getAuthor());
        Comment comment = commentService.createComment(commentDTO);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestParam Long userId, @RequestBody String content) {
        Comment updatedComment = commentService.updateComment(commentId, userId, content);
        return ResponseEntity.ok(updatedComment);
    }

    @PostMapping("/{commentId}/react")
    public ResponseEntity<String> reactToComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam boolean isLike) {

        String message = commentService.toggleReaction(commentId, userId, isLike);
        commentService.updateLikeDislikeCounts(commentId);
        return ResponseEntity.ok(message);
    }
}