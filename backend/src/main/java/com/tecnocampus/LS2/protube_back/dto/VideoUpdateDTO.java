package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoUpdateDTO {
    private String title;
    private String description;
    private String username; // Add username for ownership validation
}
