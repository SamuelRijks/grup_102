package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.repository.CategoryRepository;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, CommentRepository commentRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;

    }


    public VideoDetailsDTO getVideoDetailsById(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            System.out.println("Video not found");
            return null;
        }
        return new VideoDetailsDTO(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<VideoSummaryDTO> getAllVideoSummaries() {
        return videoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VideoSummaryDTO convertToDTO(Video video) {
        VideoSummaryDTO dto = new VideoSummaryDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setUploaderUsername(video.getUploader().getUsername());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        return dto;
    }

    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor().getUsername());
        dto.setTimestamp(comment.getTimestamp());
        dto.setLikes(comment.getLikes());
        dto.setDislikes(comment.getDislikes());
        return dto;
    }

    public void createVideoWithFileAndUser(Long id, String title, String fileName, Long userId,
                                           Integer height, Integer width, Double duration,
                                           String thumbnailPath, String description) {
        // Find the user by ID
        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));

        // Create the video
        Video video = new Video();
        video.setId(id);
        video.setTitle(title);
        video.setUrl("/api/videos/" + fileName); // Set the public URL for the video
        video.setUploadDate(LocalDateTime.now());
        video.setUploader(uploader); // Associate the user as the uploader
        video.setHeight(height);
        video.setWidth(width);
        video.setDuration(duration); // Set the duration
        video.setThumbnailUrl("/api/images/" + id + ".webp"); // Set the public URL for the thumbnail
        video.setLikes(0);
        video.setDislikes(0);
        video.setViews(0);

        // Create and set metadata
        Meta meta = new Meta();
        meta.setDescription(description); // Assign the description
        video.setMeta(meta);

        // Save the video in the database
        videoRepository.save(video);
    }

    public Long getNextVideoId() {
        return videoRepository.findAll().stream()
                .mapToLong(Video::getId)
                .max()
                .orElse(0L) + 1;
    }
}