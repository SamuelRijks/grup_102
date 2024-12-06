package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CommentRepositoryTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService; // Assuming you have a service layer

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExistsByContentAndAuthorAndVideo() {
        User author = new User();
        author.setUsername("testUser");
        Video video = new Video();
        video.setTitle("testVideo");
        Comment comment = new Comment();
        comment.setContent("testContent");
        comment.setAuthor(author);
        comment.setVideo(video);
        comment.setTimestamp(LocalDateTime.now());

        when(commentRepository.existsByContentAndAuthorAndVideo("testContent", author, video)).thenReturn(true);

        boolean exists = commentRepository.existsByContentAndAuthorAndVideo("testContent", author, video);
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindByContentAndAuthorAndVideo() {
        User author = new User();
        author.setUsername("testUser");
        Video video = new Video();
        video.setTitle("testVideo");
        Comment comment = new Comment();
        comment.setContent("testContent");
        comment.setAuthor(author);
        comment.setVideo(video);
        comment.setTimestamp(LocalDateTime.now());

        when(commentRepository.findByContentAndAuthorAndVideo("testContent", author, video)).thenReturn(Optional.of(comment));

        Optional<Comment> foundComment = commentRepository.findByContentAndAuthorAndVideo("testContent", author, video);
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("testContent");
    }
}