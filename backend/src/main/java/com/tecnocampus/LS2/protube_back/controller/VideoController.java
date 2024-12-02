package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoUploadDTO;
import com.tecnocampus.LS2.protube_back.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {


    private final VideoService videoService;
    private final Path videoLocation;

    @Autowired
    public VideoController(VideoService videoService, @Value("${pro_tube.store.dir}") String storeDir) {
        this.videoService = videoService;
        this.videoLocation = Paths.get(storeDir);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename) {
        try {
            Path file = videoLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4") // Asegura el tipo de contenido
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"") // Cambiado a 'inline'
                        .body(resource);
               } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
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

    @PostMapping("/upload")
    public ResponseEntity<Video> uploadVideo(@RequestBody VideoUploadDTO videoUploadDTO) {
        Video video = videoService.createVideo(videoUploadDTO);
        return ResponseEntity.ok(video);
    }
}