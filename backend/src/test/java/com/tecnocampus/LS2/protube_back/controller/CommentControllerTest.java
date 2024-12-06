package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.service.CommentService;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/*
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void testAddComment() throws Exception {
        // Arrange
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("This is a comment");
        commentDTO.setVideoId(1L);
        commentDTO.setAuthor("Proximity");
        commentDTO.setUserId(1L);
        commentDTO.setTimestamp(LocalDateTime.parse("2024-11-01T10:00:00"));
        commentDTO.setLikes(0);
        commentDTO.setDislikes(0);

        Comment comment = new Comment();
        comment.setContent("This is a comment");
        comment.setVideo(new Video());
        comment.setAuthor(new User());
        commentDTO.setTimestamp(LocalDateTime.parse("2024-11-01T10:00:00"));

        when(commentService.createComment(commentDTO)).thenReturn(comment);

        // Act & Assert
        mockMvc.perform(post("/api/comments/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"content\": \"This is a comment\", \"videoId\": 1, \"author\": \"Proximity\", \"userId\": 1, \"timestamp\": \"2024-11-01T10:00:00\", \"likes\": 0, \"dislikes\": 0 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("This is a comment"))
                .andExpect(jsonPath("$.author").value("Proximity"));
    }

    @Test
    public void testDeleteComment() throws Exception {
        // Arrange
        Long commentId = 1L;
        Long userId = 1L;

        doNothing().when(commentService).deleteComment(commentId, userId);

        // Act & Assert
        mockMvc.perform(delete("/api/comments/delete/{commentId}", commentId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(commentId, userId);
    }

    @Test
    public void testUpdateComment() throws Exception {
        // Arrange
        Long commentId = 1L;
        Long userId = 1L;
        String content = "Updated comment content";

        Comment updatedComment = new Comment();
        updatedComment.setContent(content);

        when(commentService.updateComment(commentId, userId, content)).thenReturn(updatedComment);

        // Act & Assert
        mockMvc.perform(put("/api/comments/update/{commentId}", commentId)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Updated comment content\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated comment content"));
    }

    @Test
    public void testLikeComment() throws Exception {
        // Arrange
        Long commentId = 1L;
        Long userId = 1L;

        doNothing().when(commentService).likeComment(commentId, userId);

        // Act & Assert
        mockMvc.perform(post("/api/comments/{commentId}/like", commentId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

        verify(commentService, times(1)).likeComment(commentId, userId);
    }

    @Test
    public void testDislikeComment() throws Exception {
        // Arrange
        Long commentId = 1L;
        Long userId = 1L;

        doNothing().when(commentService).dislikeComment(commentId, userId);

        // Act & Assert
        mockMvc.perform(post("/api/comments/{commentId}/dislike", commentId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());

        verify(commentService, times(1)).dislikeComment(commentId, userId);
    }
}*/
