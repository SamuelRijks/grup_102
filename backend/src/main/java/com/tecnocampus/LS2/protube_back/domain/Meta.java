package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.*;
import java.util.List;

@Embeddable
public class Meta {
    @Column(length = 5000)
    private String description;

    @ManyToMany
    private List<Category> categories; // Cambiar a objetos de Category

    @ManyToMany
    private List<Tag> tags; // Cambiar a objetos de Tag

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Comment> comments;

    // Getters y Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
