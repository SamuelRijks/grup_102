package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDetailsDTO {
    private String title;
    private String description;
    private String uploaderUsername;
    private String videoUrl;
}