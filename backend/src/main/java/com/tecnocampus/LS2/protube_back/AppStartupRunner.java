package com.tecnocampus.LS2.protube_back;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.domain.*;
import com.tecnocampus.LS2.protube_back.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.security.SecureRandom;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);

    private final Path rootPath;
    private final Boolean loadInitialData;
    private List<String> videoList;

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public AppStartupRunner(Environment env, VideoRepository videoRepository, UserRepository userRepository,
                            CategoryRepository categoryRepository, TagRepository tagRepository, CommentRepository commentRepository) {
        String storeDir = env.getProperty("pro_tube.store.dir");
        this.rootPath = Paths.get(storeDir);
        this.loadInitialData = env.getProperty("pro_tube.load_initial_data", Boolean.class);
        this.videoList = new ArrayList<>();
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    public List<String> getVideoList() {
        return videoList;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (loadInitialData) {
//            commentRepository.deleteAll(); // Primero elimina los comentarios
//            userRepository.deleteAll();    // Luego elimina los usuarios
//            videoRepository.deleteAll();
//            categoryRepository.deleteAll();
//            tagRepository.deleteAll();
            loadVideosFromDirectory();
        }
    }

    private void loadVideosFromDirectory() {
        try {
            LOG.info("Starting to load videos from directory: {}", rootPath);
            if (!Files.exists(rootPath)) {
                LOG.error("The root path does not exist: {}", rootPath);
                return;
            }
            if (!Files.isDirectory(rootPath)) {
                LOG.error("The root path is not a directory: {}", rootPath);
                return;
            }
            Files.walk(rootPath)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".json")) // Filter only JSON files
                    .forEach(file -> {
                        LOG.info("Found JSON file: {}", file.getFileName().toString());

                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            JsonNode rootNode = objectMapper.readTree(file.toFile());
                            Long videoId = rootNode.path("id").asLong();
                            String videoTitle = rootNode.path("title").asText();

                            Optional<Video> existingVideoOpt = videoRepository.findById(videoId);
                            Video video;
                            if (existingVideoOpt.isPresent()) {
                                video = existingVideoOpt.get();
                                LOG.info("Video already exists: ID={}, Title={}", videoId, videoTitle);
                            } else {
                                video = new Video();
                                video.setId(videoId);
                                video.setTitle(videoTitle);
                                // Set the URL to the corresponding MP4 file
                                String mp4FileName = file.getFileName().toString().replace(".json", ".mp4");
                                Path mp4FilePath = rootPath.resolve(mp4FileName);
                                if (Files.exists(mp4FilePath)) {
                                    video.setUrl(mp4FilePath.toUri().toString());
                                } else {
                                    LOG.warn("MP4 file not found for video: {}", videoTitle);
                                }
                                video.setWidth(rootNode.path("width").asInt());
                                video.setHeight(rootNode.path("height").asInt());
                                video.setDuration(rootNode.path("duration").asDouble());
                                video.setUploadDate(LocalDateTime.now());

                                // Set thumbnail URL
                                String thumbnailFileName = file.getFileName().toString().replace(".json", ".webp");
                                Path thumbnailPath = rootPath.resolve(thumbnailFileName);
                                if (Files.exists(thumbnailPath)) {
                                    video.setThumbnailUrl(thumbnailPath.toUri().toString());
                                } else {
                                    LOG.warn("Thumbnail file not found for video: {}", videoTitle);
                                }

                                // Set uploader
                                String uploaderName = rootNode.path("user").asText();
                                User uploader = userRepository.findByUsername(uploaderName)
                                        .orElseGet(() -> {
                                            User newUser = new User();
                                            newUser.setUsername(uploaderName);
                                            newUser.setEmail(uploaderName + "@example.com");
                                            newUser.setPassword(generateRandomPassword());
                                            return userRepository.save(newUser);
                                        });
                                video.setUploader(uploader);

                                // Create and set Meta object
                                Meta meta = new Meta();
                                meta.setDescription(rootNode.path("meta").path("description").asText());
                                video.setMeta(meta);
                            }
                            videoRepository.save(video);

                            // Process comments
                            List<Comment> comments = new ArrayList<>();
                            JsonNode commentsNode = rootNode.path("meta").path("comments");
                            if (commentsNode.isArray()) {
                                for (JsonNode commentNode : commentsNode) {
                                    try {
                                        String authorName = commentNode.path("author").asText();
                                        User commentAuthor = userRepository.findByUsername(authorName)
                                                .orElseGet(() -> {
                                                    User newUser = new User();
                                                    newUser.setUsername(authorName);
                                                    newUser.setEmail(authorName + "@example.com");
                                                    newUser.setPassword(generateRandomPassword());
                                                    return userRepository.save(newUser);
                                                });
                                        Comment comment = new Comment();
                                        comment.setContent(commentNode.path("text").asText());
                                        comment.setAuthor(commentAuthor);
                                        comment.setVideo(video);
                                        comment.setTimestamp(LocalDateTime.now());

                                        // Verificar si el comentario ya existe en la base de datos
                                        if (!commentRepository.existsByContentAndAuthorAndVideo(comment.getContent(), comment.getAuthor(), comment.getVideo())) {
                                            // Si no existe, guardar el nuevo comentario
                                            commentRepository.save(comment);
                                            comments.add(comment);
                                            LOG.info("Saved new comment: {} by {}", comment.getContent(), comment.getAuthor().getUsername());
                                        } else {
                                            // Si existe, actualizar los likes y dislikes si han cambiado
                                            Optional<Comment> existingCommentOpt = commentRepository.findByContentAndAuthorAndVideo(comment.getContent(), comment.getAuthor(), comment.getVideo());
                                            if (existingCommentOpt.isPresent()) {
                                                Comment existingComment = existingCommentOpt.get();
                                                boolean updated = false;

                                                // Verificar si los likes han cambiado
                                                if (existingComment.getLikes() != commentNode.path("likes").asInt()) {
                                                    existingComment.setLikes(commentNode.path("likes").asInt());
                                                    updated = true;
                                                }

                                                // Verificar si los dislikes han cambiado
                                                if (existingComment.getDislikes() != commentNode.path("dislikes").asInt()) {
                                                    existingComment.setDislikes(commentNode.path("dislikes").asInt());
                                                    updated = true;
                                                }

                                                // Si se detectó un cambio, actualizar el comentario en la base de datos
                                                if (updated) {
                                                    commentRepository.save(existingComment);
                                                    LOG.info("Updated likes/dislikes for comment: {}", existingComment.getContent());
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        LOG.error("Failed to process comment for video {}: {}", video.getId(), e.getMessage());
                                    }
                                }
                            }
                            videoRepository.save(video);

                            // Set comments to video and meta
                            video.setComments(comments); // Asignar comentarios al video
                            video.getMeta().setComments(comments);  // Asignar comentarios al meta

                            videoRepository.save(video);

                            // Process categories
                            List<Category> categories = new ArrayList<>();
                            JsonNode categoriesNode = rootNode.path("meta").path("categories");
                            if (categoriesNode.isArray()) {
                                for (JsonNode categoryNode : categoriesNode) {
                                    String categoryName = categoryNode.asText();
                                    Category category = categoryRepository.findByName(categoryName)
                                            .orElseGet(() -> {
                                                Category newCategory = new Category();
                                                newCategory.setName(categoryName);
                                                return categoryRepository.save(newCategory);
                                            });
                                    categories.add(category);
                                }
                            }
                            video.setCategories(categories); // Sigue asignando categorías al video
                            video.getMeta().setCategories(categories);  // También asigna categorías al meta

                            // Process tags
                            List<Tag> tags = new ArrayList<>();
                            JsonNode tagsNode = rootNode.path("meta").path("tags");
                            if (tagsNode.isArray()) {
                                for (JsonNode tagNode : tagsNode) {
                                    String tagName = tagNode.asText();
                                    Tag tag = tagRepository.findByName(tagName)
                                            .orElseGet(() -> {
                                                Tag newTag = new Tag();
                                                newTag.setName(tagName);
                                                return tagRepository.save(newTag);
                                            });
                                    tags.add(tag);
                                }
                            }
                            video.setTags(tags); // Sigue asignando etiquetas al video
                            video.getMeta().setTags(tags);  // También asigna etiquetas al meta

                            // Save video
                            videoRepository.save(video);

                        } catch (IOException e) {
                            LOG.error("Error parsing JSON file: {}", e.getMessage());
                        }
                    });
            LOG.info("Loaded {} videos", videoRepository.count());
        } catch (IOException e) {
            LOG.error("Error loading videos: {}", e.getMessage());
        }
    }


    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 12; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }
}