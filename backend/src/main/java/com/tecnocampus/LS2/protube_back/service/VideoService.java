package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoDetails(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public List<VideoSummaryDTO> getAllVideoSummaries() {
        return videoRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private VideoSummaryDTO convertToDTO(Video video) {
        VideoSummaryDTO dto = new VideoSummaryDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setUploaderUsername(video.getUploader().getUsername());
        dto.setThumbnailUrl(video.getThumbnailUrl());
        return dto;
    }
}