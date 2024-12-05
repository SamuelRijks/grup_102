package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoUploadDTO;
import com.tecnocampus.LS2.protube_back.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final TagService tagService;
    private final UserRepository userRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, CommentRepository commentRepository, CategoryRepository categoryRepository, TagService tagService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.tagService = tagService;
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

    public Video uploadVideo(VideoUploadDTO videoUploadDTO) {
        // Validate User
        User uploader = userRepository.findById(videoUploadDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<String> categoryNames = videoUploadDTO.getCategories();
        System.out.println("Original categoryNames before trimming: " + categoryNames);
        System.out.println("categoryNames is null: " + (categoryNames == null));

        // Check if categoryNames is effectively empty
        boolean isEffectivelyEmpty = categoryNames == null || categoryNames.isEmpty() ||
                categoryNames.stream().allMatch(name -> name.trim().isEmpty() || name.equals("[]"));

        if (isEffectivelyEmpty) {
            categoryNames = Collections.emptyList(); // Assign an empty list
            System.out.println("categoryNames is null or effectively empty");
        } else {
            System.out.println("categoryNames is not null or effectively empty");
            // Process the non-empty categoryNames
            categoryNames = categoryNames.stream()
                    .map(name -> name.substring(1, name.length() - 1))
                    .flatMap(name -> Arrays.stream(name.replaceAll("\\[\\[|\\]\\]", "").split(","))) // Remove outer brackets and split by comma
                    .map(String::trim) // Remove leading/trailing spaces
                    .map(name -> name.replace("\"", "")) // Remove quotes if they exist
                    .collect(Collectors.toList());
        }

        // Print processed categoryNames
        System.out.println("Processed categoryNames: " + categoryNames);

        List<Category> categories = null;
        if (!categoryNames.isEmpty()) {
            categories = categoryRepository.findAllByNameIn(categoryNames);
            if (categories.isEmpty()) {
                throw new IllegalArgumentException("Some category names are invalid or not found.");
            }
        }


        // Validate Tags
        List<Tag> tags = null;
        if (videoUploadDTO.getTags() != null && !videoUploadDTO.getTags().isEmpty()) {
            tags = tagService.createOrFetchTags(videoUploadDTO.getTags());
        }

        Long nextId = getNextVideoId();
        System.out.println("nextId: " + nextId);

        // Create Video
        Video video = new Video();
        video.setId(nextId);
        video.setUploader(uploader);
        video.setTitle(videoUploadDTO.getTitle());
        video.setUrl(videoUploadDTO.getUrl());
        video.setThumbnailUrl(videoUploadDTO.getThumbnailUrl());
        video.setWidth(videoUploadDTO.getWidth());
        video.setHeight(videoUploadDTO.getHeight());
        video.setDuration(videoUploadDTO.getDuration());


        Meta meta = new Meta();
        meta.setDescription(videoUploadDTO.getDescription());
        video.setCategories(categories);
        video.setTags(tags);
        video.setMeta(meta);

        return videoRepository.save(video);
    }


    private VideoSummaryDTO convertToDTO(Video video) {
        VideoSummaryDTO dto = new VideoSummaryDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setUploaderUsername(video.getUploader().getUsername());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        return dto;
    }

    public Video updateVideo(Long videoId, VideoUploadDTO videoUploadDTO) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        User user = userRepository.findById(videoUploadDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!video.getUploader().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to update this video");
        }

        video.setTitle(videoUploadDTO.getTitle());
        video.getMeta().setDescription(videoUploadDTO.getDescription());

        List<Category> categories = null;
        if (videoUploadDTO.getCategories() != null && !videoUploadDTO.getCategories().isEmpty()) {
            categories = categoryRepository.findAllByNameIn(videoUploadDTO.getCategories());
            if (categories.size() != videoUploadDTO.getCategories().size()) {
                throw new IllegalArgumentException("Some category names are invalid or not found.");
            }
        }

        List<Tag> tags = null;
        if (videoUploadDTO.getTags() != null && !videoUploadDTO.getTags().isEmpty()) {
            tags = tagService.createOrFetchTags(videoUploadDTO.getTags());
        }

        return videoRepository.save(video);
    }

    public void likeVideo(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        // Logic to check if the user has already liked the video can be added here
        video.setLikes(video.getLikes() + 1);
        videoRepository.save(video);
    }

    public void dislikeVideo(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        // Logic to check if the user has already disliked the video can be added here
        video.setDislikes(video.getDislikes() + 1);
        videoRepository.save(video);
    }


    @Transactional(readOnly = true)
    public Long getNextVideoId() {
        Long maxId = videoRepository.findMaxId(); // Assuming findMaxId() is implemented
        return (maxId != null ? maxId : 0L) + 1;
    }
}