package com.tecnocampus.LS2.protube_back.dto;

import com.tecnocampus.LS2.protube_back.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class VideoDetailsDTO {
    private Long id;
    private String title;
    private int width;
    private int height;
    private double duration;
    private String uploaderUsername;
    private String description;
    private List<Category> categories;
    private List<Tag> tags;
    private List<CommentDTO> comments;
    private String videoUrl;

    public VideoDetailsDTO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.width = video.getWidth();
        this.height = video.getHeight();
        this.duration = video.getDuration() != null ? video.getDuration() : 0.0; // Manejar el caso de null
        this.uploaderUsername = video.getUploader().getUsername();
        setVideoUrl(video.getUrl());

        Meta meta = video.getMeta();
        if (meta != null) {
            System.out.println("META NO EMPTY ");
            this.description = meta.getDescription();
            this.categories = meta.getCategories().stream()
                    .map(category -> {
                        Category categoryDTO = new Category();
                        categoryDTO.setName(category.getName());
                        return categoryDTO;
                    }).collect(Collectors.toList());
            this.tags = meta.getTags().stream()
                    .map(tag -> {
                        Tag tagDTO = new Tag();
                        tagDTO.setName(tag.getName());
                        return tagDTO;
                    }).collect(Collectors.toList());
            this.comments = video.getComments().stream()
                .map(comment -> {
                    CommentDTO commentDTO = new CommentDTO(comment);
                    return commentDTO;
                }).collect(Collectors.toList());
        } else {
            System.out.println("META  EMPTY ");
            this.description = "No description available.";
            this.categories = List.of();
            this.tags = List.of();
            this.comments = List.of();
        }

    }

    public void setVideoUrl(String filename) {
        System.out.println("Setting video URL to: " + filename);
        this.videoUrl = "/api/videos/" + filename;
        System.out.println("Video URL set to: " + this.videoUrl);
    }
}