package com.tecnocampus.LS2.protube_back.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class VideoSummaryDTO {
    private Long id;
    private String title;
    private String uploaderUsername;
    private String thumbnailUrl;

    public void setThumbnailUrl(String filename) {
        this.thumbnailUrl = "/api/images/" + filename;
    }
    public VideoSummaryDTO(Long id, String title,   String uploaderUsername, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.uploaderUsername=uploaderUsername;
        this.thumbnailUrl = thumbnailUrl;
    }
}