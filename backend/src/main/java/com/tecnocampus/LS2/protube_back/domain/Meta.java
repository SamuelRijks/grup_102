package com.tecnocampus.LS2.protube_back.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.List;

@Embeddable
public class Meta {
    private String description;

    @ElementCollection
    private List<String> categories;

    @ElementCollection
    private List<String> tags;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Comment> comments;

    // Getters i Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

