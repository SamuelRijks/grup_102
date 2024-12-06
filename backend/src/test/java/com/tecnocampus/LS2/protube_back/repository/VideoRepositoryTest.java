package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class VideoRepositoryTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService; // Assuming you have a service layer

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByTitle() {
        Video video = new Video();
        video.setTitle("Test Video");

        when(videoRepository.findByTitle("Test Video")).thenReturn(Optional.of(video));

        Optional<Video> foundVideoOptional = videoRepository.findByTitle("Test Video");
        Video foundVideo = foundVideoOptional.orElse(null);
        assertThat(foundVideo).isNotNull();
        assertThat(foundVideo.getTitle()).isEqualTo("Test Video");
    }

    @Test
    public void testFindMaxId() {
        when(videoRepository.findMaxId()).thenReturn(10L);

        Long maxId = videoRepository.findMaxId();
        assertThat(maxId).isEqualTo(10L);
    }
}