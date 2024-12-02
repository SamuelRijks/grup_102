package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoUploadDTO;
import com.tecnocampus.LS2.protube_back.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Video createVideo(VideoUploadDTO videoUploadDTO) {
        Video video = new Video();

        userRepository.findById(videoUploadDTO.getUserid()).ifPresent(video::setUploader);
        video.setTitle(videoUploadDTO.getTitle());
        video.setUrl(videoUploadDTO.getUrl());
        video.setThumbnailUrl(videoUploadDTO.getThumbnailUrl());
        video.setMeta(new Meta());
        video.getMeta().setDescription(videoUploadDTO.getDescription());

        List<Category> categories = categoryRepository.findAllById(videoUploadDTO.getCategoryIds());
        video.setCategories(categories);

        List<Tag> tags = tagService.createOrFetchTags(videoUploadDTO.getTagIds());
        video.setTags(tags);

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

    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor().getUsername());
        dto.setTimestamp(comment.getTimestamp());
        dto.setLikes(comment.getLikes());
        dto.setDislikes(comment.getDislikes());
        return dto;
    }
}