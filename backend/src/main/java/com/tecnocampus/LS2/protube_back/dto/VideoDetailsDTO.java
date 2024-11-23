package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VideoDetailsDTO {
    private Long id;
    private String title;
    private int width;
    private int height;
    private double duration;
    private String uploaderUsername;
    private String description; // Ara es desglossa de `meta`
    private List<String> categories;
    private List<String> tags;
    private List<CommentDTO> comments;
}