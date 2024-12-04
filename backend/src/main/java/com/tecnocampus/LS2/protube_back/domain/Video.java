package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video {
    @Id
    private Long id;

    private String title;
    private String url;
    private String thumbnailUrl;
    private Integer width; // Change to Integer to allow null values
    private Integer height; // Change to Integer to allow null values
    private Double duration;
    private LocalDateTime uploadDate;
    private int likes = 0;
    private int dislikes = 0;
    private int views = 0;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "video_tags",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @ManyToMany
    @JoinTable(
            name = "video_categories",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;


    @Getter
    @Setter
    @Embedded
    private Meta meta;
}

