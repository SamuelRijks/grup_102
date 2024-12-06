package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment sampleComment;

    @BeforeEach
    void setUp() {
        User sampleUser = new User();
        sampleUser.setId(1111L);
        sampleUser.setUsername("TestUser");

        Video sampleVideo = new Video();
        sampleVideo.setId(2222L);
        sampleVideo.setTitle("Test Video");

        sampleComment = new Comment();
        sampleComment.setId(1L);
        sampleComment.setAuthor(sampleUser);
        sampleComment.setContent("This is a test comment.");
        sampleComment.setVideo(sampleVideo);
    }

    @Test
    public void testGetCommentById_Found() {
        when(commentService.findById(1L)).thenReturn(Optional.of(sampleComment));

        ResponseEntity<?> response = commentController.getCommentById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); // Use HttpStatus.OK for comparison
        assertThat(response.getBody()).isInstanceOf(CommentDTO.class);

        CommentDTO commentDTO = (CommentDTO) response.getBody();
        assertThat(commentDTO.getAuthor()).isEqualTo("TestUser");
        assertThat(commentDTO.getContent()).isEqualTo("This is a test comment.");
        assertThat(commentDTO.getVideoId()).isEqualTo(2222L); // Check video ID if included in DTO
    }


    @Test
    public void testGetCommentById_NotFound() {
        when(commentService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = commentController.getCommentById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();

        assertThat(errorResponse).containsEntry("error", "Comment not found");
    }


    @Test
    public void testAddComment() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setAuthor("NewUser");
        commentDTO.setContent("New comment content");

        when(commentService.createComment(commentDTO)).thenReturn(sampleComment);

        ResponseEntity<Comment> response = commentController.addComment(commentDTO);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sampleComment);
    }

    @Test
    public void testDeleteComment() {
        doNothing().when(commentService).deleteComment(1L, 2L);

        ResponseEntity<Void> response = commentController.deleteComment(1L, 2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(commentService, times(1)).deleteComment(1L, 2L);
    }

    @Test
    public void testUpdateComment() {
        when(commentService.updateComment(1L, 2L, "Updated content")).thenReturn(sampleComment);

        ResponseEntity<Comment> response = commentController.updateComment(1L, 2L, "Updated content");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sampleComment);
    }

    @Test
    public void testReactToComment() {
        when(commentService.toggleReaction(1L, 2L, true)).thenReturn("Reaction added");
        doNothing().when(commentService).updateLikeDislikeCounts(1L);

        ResponseEntity<String> response = commentController.reactToComment(1L, 2L, true);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Reaction added");
        verify(commentService, times(1)).updateLikeDislikeCounts(1L);
    }
}
