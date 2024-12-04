package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoController {


    private final VideoService videoService;
    private final Path videoLocation;
    private final Environment env;

    @Autowired
    public VideoController(VideoService videoService, Environment env, @Value("${pro_tube.store.dir}") String storeDir) {
        this.videoService = videoService;
        this.env = env;
        this.videoLocation = Paths.get(storeDir);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename) {
        try {
            Path file = videoLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
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
    public ResponseEntity<Map<String, String>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("userId") Long userId) {
        System.out.println("Title: " + title);
        System.out.println("UserId: " + userId);
        System.out.println("File: " + file.getOriginalFilename());

        try {
            // Validate the file extension
            if (!file.getOriginalFilename().endsWith(".mp4")) {
                return ResponseEntity.badRequest().body(Map.of("error", "El fitxer no és un .mp4 vàlid."));
            }

            // Get the next available video ID
            Long nextId = videoService.getNextVideoId();

            // Save the MP4 file
            String fileName = nextId + ".mp4";
            Path filePath = Paths.get(env.getProperty("pro_tube.store.dir"), fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved video file at: " + filePath);

            // Extract video dimensions
            Map<String, Object> metadata = extractVideoMetadata(filePath.toString());
            Integer width = (Integer) metadata.get("width");
            Integer height = (Integer) metadata.get("height");
            Double duration = (Double) metadata.get("duration");

            System.out.println("Extracted Metadata - Width: " + width + ", Height: " + height + ", Duration: " + duration);

            // Generate and save the thumbnail
            String thumbnailPath = env.getProperty("pro_tube.store.dir") + "/" + nextId + ".webp";
            generateThumbnail(filePath.toString(), thumbnailPath, "00:00:05");
            System.out.println("Generated Thumbnail at: " + thumbnailPath);

            // Create metadata JSON
            String jsonPath = env.getProperty("pro_tube.store.dir") + "/" + nextId + ".json";
            createMetadataJson(jsonPath, nextId, title, userId.toString(), width, height, duration, "", List.of(), List.of(), List.of(), fileName, thumbnailPath);
            System.out.println("Saved metadata JSON at: " + jsonPath);


            videoService.createVideoWithFileAndUser(
                    nextId,
                    title,
                    fileName,
                    userId,
                    height,
                    width,
                    duration,
                    "/api/images/" + nextId + ".webp", // Thumbnail URL
                    "" // Meta description
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Vídeo carregat i desat correctament.",
                    "mp4", fileName,
                    "thumbnail", thumbnailPath,
                    "json", jsonPath
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error en desar el vídeo: " + e.getMessage()));
        }
    }

    private void createMetadataJson(String jsonPath, Long id, String title, String user,
                                    Integer width, Integer height, Double duration,
                                    String description, List<String> categories,
                                    List<String> tags, List<Map<String, String>> comments,
                                    String videoFile, String thumbnailFile) throws Exception {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", id);
        metadata.put("title", title);
        metadata.put("user", user);
        metadata.put("width", width);
        metadata.put("height", height);
        metadata.put("duration", duration);

        // Create the "meta" object
        Map<String, Object> meta = new HashMap<>();
        meta.put("description", description);
        meta.put("categories", categories);
        meta.put("tags", tags);
        meta.put("comments", comments);

        metadata.put("meta", meta);

        // Write JSON to file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(Paths.get(jsonPath).toFile(), metadata);
    }

    private Map<String, Object> extractVideoMetadata(String videoPath) throws Exception {
        // Extract width and height
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-select_streams", "v:0",
                "-show_entries", "stream=width,height",
                "-of", "csv=p=0",
                videoPath
        );

        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String output = reader.readLine(); // Example: "1280,720"
        int width, height;
        if (output != null) {
            String[] dimensions = output.split(",");
            width = Integer.parseInt(dimensions[0].trim());
            height = Integer.parseInt(dimensions[1].trim());
        } else {
            throw new RuntimeException("Failed to extract video dimensions. No output from ffprobe.");
        }

        // Extract duration
        processBuilder = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "csv=p=0",
                videoPath
        );

        process = processBuilder.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        output = reader.readLine(); // Example: "23.456"
        double duration;
        if (output != null) {
            duration = Double.parseDouble(output.trim());
        } else {
            throw new RuntimeException("Failed to extract video duration. No output from ffprobe.");
        }

        // Return metadata as a Map
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("width", width);
        metadata.put("height", height);
        metadata.put("duration", duration);
        return metadata;
    }

    private void generateThumbnail(String videoPath, String thumbnailPath, String timestamp) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-ss", timestamp, // Set the timestamp for the thumbnail (e.g., 5 seconds into the video)
                "-vframes", "1",  // Extract one frame
                "-vf", "scale=320:-1", // Resize the thumbnail to 320px width while maintaining aspect ratio
                thumbnailPath
        );

        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to generate thumbnail.");
        }
    }
}