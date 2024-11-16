package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VideoSummaryDTO {
    private Long id;
    private String title;
    private String uploaderUsername;
    private String thumbnailUrl;

    // Getters and Setters
}