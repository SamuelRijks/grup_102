package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository;
    }

    public VideoDetailsDTO getVideoDetailsById(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }

        VideoDetailsDTO dto = new VideoDetailsDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getMeta() != null ? video.getMeta().getDescription() : "No description available");
        dto.setWidth(1920); // Establecer width a 1920
        dto.setHeight(1080); // Establecer height a 1080
        System.out.println("video.getDuration() = " + video.getDuration());
        dto.setDuration(video.getDuration() != null ? video.getDuration() : 0.0); // Manejar el caso de null
        dto.setUploaderUsername(video.getUploader().getUsername());
        dto.setVideoUrl("/api/videos/" + video.getId() + ".mp4"); // Establecer la URL del video

        Meta meta = video.getMeta();
        if (meta != null) {
            dto.setDescription(meta.getDescription());
            dto.setCategories(meta.getCategories());
            dto.setTags(meta.getTags());

            List<CommentDTO> commentDTOs = meta.getComments().stream()
                    .map(comment -> {
                        CommentDTO commentDTO = new CommentDTO();
                        commentDTO.setContent(comment.getContent());
                        commentDTO.setAuthor(comment.getAuthor().getUsername());
                        return commentDTO;
                    }).toList();
            dto.setComments(commentDTOs);
        } else {
            dto.setDescription("No description available.");
            dto.setCategories(List.of());
            dto.setTags(List.of());
            dto.setComments(List.of());
        }

        return dto;
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
}