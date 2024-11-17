package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GetMapping("/summaries")
    public List<VideoSummaryDTO> getAllVideoSummaries() {
        return videoService.getAllVideoSummaries();
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<VideoDetailsDTO> getVideoDetails(@PathVariable Long id) {
        VideoDetailsDTO videoDetails = videoService.getVideoDetailsById(id);
        if (videoDetails != null) {
            return ResponseEntity.ok(videoDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}