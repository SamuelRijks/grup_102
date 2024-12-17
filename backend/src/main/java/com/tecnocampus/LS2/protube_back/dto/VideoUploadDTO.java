package com.tecnocampus.LS2.protube_back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class VideoUploadDTO {

    @NotNull(message = "User ID cannot be null")
    private String username;

    @NotBlank(message = "The title cannot be empty")
    private String title;

    @NotNull(message = "File cannot be null")
    private MultipartFile file; // Representing the uploaded file

    private String description;

    private String url; // Populated by the controller

    private String thumbnailUrl;

    private List<String> categories;

    private List<String> tags;

    private Integer width;
    private Integer height;
    private Double duration;

    // Constructors, getters, and setters (if needed)
}
