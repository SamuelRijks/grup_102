package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.Category;
import com.tecnocampus.LS2.protube_back.domain.Meta;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.VideoDetailsDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoUpdateDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoUploadDTO;
import com.tecnocampus.LS2.protube_back.repository.CategoryRepository;
import com.tecnocampus.LS2.protube_back.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/videos")
public class VideoController {


    private final VideoService videoService;
    private final Path videoLocation;
    private final CategoryRepository categoryRepository;

    @Autowired
    public VideoController(VideoService videoService, @Value("${pro_tube.store.dir}") String storeDir, CategoryRepository categoryRepository) {
        this.videoService = videoService;
        this.videoLocation = Paths.get(storeDir);
        this.categoryRepository = categoryRepository;
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
            System.out.println("Categories " + videoDetails.getCategories());
            System.out.println("Tags " + videoDetails.getTags());
            return ResponseEntity.ok(videoDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadVideo(@ModelAttribute VideoUploadDTO videoUploadDTO) {
        System.out.println("Upload endpoint called with title: " + videoUploadDTO.getTitle());
        try {
            // Validate the file
            MultipartFile file = videoUploadDTO.getFile();
            if (file == null || !file.getOriginalFilename().toLowerCase().endsWith(".mp4")) {
                System.out.println("File is null");
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file. Must be an MP4."));
            }
            System.out.println("File received: " + file.getOriginalFilename());
            if (!file.getOriginalFilename().toLowerCase().endsWith(".mp4")) {
                System.out.println("Invalid file type: " + file.getOriginalFilename());
            }

            System.out.println("File received: " + file.getOriginalFilename());
            System.out.println("User ID: " + videoUploadDTO.getUserId());
            System.out.println("Tags: " + videoUploadDTO.getTags());

            // Generate video file name and save the file
            Long nextId = videoService.getNextVideoId();
            String fileName = nextId + ".mp4";
            Path filePath = videoLocation.resolve(nextId + ".mp4");
            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File saved at: " + filePath);
            } catch (Exception e) {
                System.out.println("Failed to save file: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Failed to save file"));
            }

            // Generate thumbnail and metadata
            String thumbnailPath = videoLocation.resolve(nextId + ".webp").toString();
            System.out.println("Thumbnailpath" + thumbnailPath);
            System.out.println("Generating thumbnail...");
            try {
                generateThumbnail(filePath.toString(), thumbnailPath, "00:00:05");
                System.out.println("Thumbnail generated at: " + thumbnailPath);
            } catch (Exception e) {
                System.out.println("Failed to generate thumbnail: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "Failed to generate thumbnail"));
            }

            // Set additional fields in DTO
            videoUploadDTO.setUrl(fileName);

            videoUploadDTO.setThumbnailUrl(normalizeUrl("/api/images", nextId + ".webp"));

            System.out.println("VideoUploadDTO url: " + videoUploadDTO.getUrl());
            System.out.println("VideoUploadDTO thumbnailUrl: " + videoUploadDTO.getThumbnailUrl());

            List<String> tags = videoUploadDTO.getTags();
            if (tags == null || tags.isEmpty()) {
                tags = null; // Assign null if empty
            }

            List<String> categories = videoUploadDTO.getCategories();
            if (categories != null && !categories.isEmpty()) {
                videoUploadDTO.setCategories(categories);
            } else {
                videoUploadDTO.setCategories(null); // Allow empty categories
            }
            categories = categories.stream()
                    .map(name -> name.substring(1, name.length() - 1))
                    .flatMap(name -> Arrays.stream(name.replaceAll("\\[\\[|\\]\\]", "").split(","))) // Remove outer brackets and split by comma
                    .map(String::trim) // Remove leading/trailing spaces
                    .map(name -> name.replace("\"", "")) // Remove quotes if they exist
                    .collect(Collectors.toList());

            System.out.println("Processed categories: " + categories);

            System.out.println("VideoUploadDTO userId: " + videoUploadDTO.getUserId());
            System.out.println("VideoUploadDTO title: " + videoUploadDTO.getTitle());
            if (videoUploadDTO.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User ID is missing or null."));
            }
            System.out.println("Received category names: " + videoUploadDTO.getCategories());
            // Save video using service

            // Extract metadata
            Map<String, Object> metadata;
            try {
                System.out.println("Extracting video metadata...");
                System.out.println("File path: " + filePath);
                System.out.println("File path.toString(): " + filePath.toString());
                metadata = extractVideoMetadata(filePath.toString());
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to extract video metadata: " + e.getMessage()));
            }

            videoUploadDTO.setWidth((Integer) metadata.get("width"));
            videoUploadDTO.setHeight((Integer) metadata.get("height"));
            videoUploadDTO.setDuration((Double) metadata.get("duration"));

            System.out.println("Saving video with metadata: " + metadata);


            System.out.println("Saving video with title: " + videoUploadDTO.getTitle());
            Video savedVideo = videoService.uploadVideo(videoUploadDTO);
            System.out.println("Saved video ID: " + savedVideo.getId());


            // Generate JSON metadata
            String jsonPath = videoLocation.resolve(nextId + ".json").toString();
            createMetadataJson(
                    jsonPath,
                    nextId,
                    videoUploadDTO.getTitle(),
                    savedVideo.getUploader().getUsername(),
                    videoUploadDTO.getWidth(),
                    videoUploadDTO.getHeight(),
                    videoUploadDTO.getDuration(),
                    videoUploadDTO.getDescription(),
                    categories,
                    tags,
                    new ArrayList<>(), // Empty comments list
                    videoUploadDTO.getUrl(),
                    videoUploadDTO.getThumbnailUrl()
            );
            System.out.println("Metadata JSON saved at: " + jsonPath);
            return ResponseEntity.ok(Map.of(
                    "message", "Video uploaded successfully.",
                    "videoId", String.valueOf(savedVideo.getId())
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload video: " + e.getMessage()));
        }
    }

    private String normalizeUrl(String baseUrl, String path) {
        if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        if (path.startsWith("/")) path = path.substring(1);
        return baseUrl + "/" + path;
    }

    private void generateThumbnail(String videoPath, String thumbnailPath, String timestamp) throws Exception {
        System.out.println("Generating thumbnail for video: " + videoPath);
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoPath,
                "-ss", timestamp, // Set the timestamp for the thumbnail (e.g., 5 seconds into the video)
                "-vframes", "1",  // Extract one frame
                "-vf", "scale=320:-1", // Resize the thumbnail to 320px width while maintaining aspect ratio
                thumbnailPath
        );
        System.out.println("Command: " + processBuilder.command());
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("ffmpeg output: " + line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println("ffmpeg error: " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg failed with exit code " + exitCode);
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

        // Corrige el formato de categorías y etiquetas
        List<String> cleanCategories = categories != null ? categories : Collections.emptyList();
        List<String> cleanTags = tags != null ? tags : Collections.emptyList();

        //trim the first and last character of the array cleanCategories
        System.out.println("cleanCategories: " + cleanCategories);
        System.out.println("cleanTags: " + cleanTags);

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
        System.out.println("Starting video metadata extraction...");
        System.out.println("Video path: " + videoPath);

        Map<String, Object> metadata = new HashMap<>();

        // Extract width and height
        try {
            ProcessBuilder dimensionsProcessBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-select_streams", "v:0",
                    "-show_entries", "stream=width,height",
                    "-of", "csv=p=0",
                    videoPath
            );

            System.out.println("Running ffprobe for dimensions: " + dimensionsProcessBuilder.command());
            Process dimensionsProcess = dimensionsProcessBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(dimensionsProcess.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(dimensionsProcess.getErrorStream()))) {

                String dimensionsOutput = reader.readLine();
                if (dimensionsOutput != null) {
                    System.out.println("Dimensions output: " + dimensionsOutput);
                    String[] dimensions = dimensionsOutput.split(",");
                    metadata.put("width", Integer.parseInt(dimensions[0].trim()));
                    metadata.put("height", Integer.parseInt(dimensions[1].trim()));
                } else {
                    System.err.println("No dimensions output from ffprobe.");
                    throw new RuntimeException("Failed to extract dimensions.");
                }

                String errorOutput;
                while ((errorOutput = errorReader.readLine()) != null) {
                    System.err.println("ffprobe error (dimensions): " + errorOutput);
                }
            }

            int dimensionsExitCode = dimensionsProcess.waitFor();
            if (dimensionsExitCode != 0) {
                throw new RuntimeException("ffprobe failed for dimensions with exit code: " + dimensionsExitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error extracting dimensions: " + e.getMessage(), e);
        }

        // Extract duration
        try {
            ProcessBuilder durationProcessBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "csv=p=0",
                    videoPath
            );

            System.out.println("Running ffprobe for duration: " + durationProcessBuilder.command());
            Process durationProcess = durationProcessBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(durationProcess.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(durationProcess.getErrorStream()))) {

                String durationOutput = reader.readLine();
                if (durationOutput != null) {
                    System.out.println("Duration output: " + durationOutput);
                    metadata.put("duration", Double.parseDouble(durationOutput.trim()));
                } else {
                    System.err.println("No duration output from ffprobe.");
                    throw new RuntimeException("Failed to extract duration.");
                }

                String errorOutput;
                while ((errorOutput = errorReader.readLine()) != null) {
                    System.err.println("ffprobe error (duration): " + errorOutput);
                }
            }

            int durationExitCode = durationProcess.waitFor();
            if (durationExitCode != 0) {
                throw new RuntimeException("ffprobe failed for duration with exit code: " + durationExitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error extracting duration: " + e.getMessage(), e);
        }

        System.out.println("Extracted metadata: " + metadata);
        return metadata;
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editVideo(@PathVariable Long id, @RequestBody VideoUpdateDTO videoUpdateDTO) {
        // Log incoming request details
        System.out.println("Received request to edit video.");
        System.out.println("Video ID: " + id);
        System.out.println("VideoUpdateDTO: " + videoUpdateDTO);

        try {
            // Fetch the video from the service
            Optional<Video> videoOpt = videoService.findById(id);

            if (videoOpt.isEmpty()) {
                System.out.println("No video found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            Video video = videoOpt.get();
            System.out.println("Fetched video details: ");
            System.out.println("Title: " + video.getTitle());
            System.out.println("Uploader: " + video.getUploader().getUsername());
            System.out.println("UpdateDTO: " + videoUpdateDTO.getUsername());

            // Check if the user attempting to edit the video matches the uploader
            if (!video.getUploader().getUsername().equals(videoUpdateDTO.getUsername())) {
                System.out.println("Forbidden: Username mismatch.");
                System.out.println("Uploader username: " + video.getUploader().getUsername());
                System.out.println("Editor username: " + videoUpdateDTO.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to edit this video.");
            }

            // Update video details
            video.setTitle(videoUpdateDTO.getTitle());
            if (video.getMeta() != null) {
                System.out.println("Updating video description...");
                video.getMeta().setDescription(videoUpdateDTO.getDescription());
            } else {
                System.out.println("Meta data is null. Creating new meta object...");
                Meta meta = new Meta();
                meta.setDescription(videoUpdateDTO.getDescription());
                video.setMeta(meta);
            }

            // Save the updated video
            videoService.save(video);
            System.out.println("Video updated successfully.");
            System.out.println("Updated title: " + video.getTitle());
            System.out.println("Updated description: " + video.getMeta().getDescription());

            return ResponseEntity.ok("Video updated successfully.");
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error occurred while editing video:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while editing the video.");
        }
    }


    @PostMapping("/{videoId}/like")
    public ResponseEntity<Void> likeVideo(@PathVariable Long videoId, @RequestParam Long userId) {
        videoService.likeVideo(videoId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{videoId}/dislike")
    public ResponseEntity<Void> dislikeVideo(@PathVariable Long videoId, @RequestParam Long userId) {
        videoService.dislikeVideo(videoId, userId);
        return ResponseEntity.ok().build();
    }
}