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
    private final CommentRepository commentRepository; // Afegir repositori de comentaris

    @Autowired
    public VideoService(VideoRepository videoRepository, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.commentRepository = commentRepository; // Injectar el repositori de comentaris
    }

    // Aquí va la implementació del mètode
    public VideoDetailsDTO getVideoDetailsById(Long id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            return null;
        }

        VideoDetailsDTO dto = new VideoDetailsDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDescription(video.getMeta() != null ? video.getMeta().getDescription() : "No description available");
        dto.setWidth(video.getWidth());
        dto.setHeight(video.getHeight());
        dto.setDuration(video.getDuration());
        dto.setUploaderUsername(video.getUploader().getUsername());
        //dto.setVideoUrl(video.getUrl());

        Meta meta = video.getMeta();
        if (meta != null) {
            dto.setDescription(meta.getDescription());
            dto.setCategories(meta.getCategories());
            dto.setTags(meta.getTags());

            // Convertir comentaris
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


    // Mètode per convertir `Comment` a `CommentDTO`
    private CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setContent(comment.getContent());
        dto.setAuthor(comment.getAuthor().getUsername()); // Nom de l'autor
        dto.setTimestamp(comment.getTimestamp());
        dto.setLikes(comment.getLikes());
        dto.setDislikes(comment.getDislikes());
        return dto;
    }
}
