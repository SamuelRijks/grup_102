package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.domain.Tag;
import com.tecnocampus.LS2.protube_back.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TagRepositoryTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService; // Assuming you have a service layer

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByName() {
        Tag tag = new Tag();
        tag.setName("TestTag");

        when(tagRepository.findByName("TestTag")).thenReturn(Optional.of(tag));

        Optional<Tag> foundTag = tagRepository.findByName("TestTag");
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getName()).isEqualTo("TestTag");
    }
}