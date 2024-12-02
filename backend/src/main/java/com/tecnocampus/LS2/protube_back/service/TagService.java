package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> createOrFetchTags(List<Long> tagIds) {
        return tagIds.stream()
                .map(tagId -> tagRepository.findById(tagId).orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setId(tagId);
                    return tagRepository.save(newTag);
                }))
                .collect(Collectors.toList());
    }
}