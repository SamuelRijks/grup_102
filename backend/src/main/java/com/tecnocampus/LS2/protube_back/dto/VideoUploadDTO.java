package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VideoUploadDTO {
    private Long userid;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private List<Long> categoryIds;
    private List<Long> tagIds;

    // Getters and setters
}