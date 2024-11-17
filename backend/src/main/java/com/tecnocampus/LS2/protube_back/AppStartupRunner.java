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
                        videoList.add(file.getFileName().toString());

                        // Parse JSON file
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            JsonNode rootNode = objectMapper.readTree(file.toFile());
                            Video video = new Video();
                            video.setTitle(rootNode.path("title").asText());
                            video.setUrl(file.toUri().toString());
                            video.setUploadDate(LocalDateTime.now());

                            // Set thumbnail URL
                            String thumbnailFileName = file.getFileName().toString().replace(".json", ".webp");
                            Path thumbnailPath = rootPath.resolve(thumbnailFileName);
                            if (Files.exists(thumbnailPath)) {
                                video.setThumbnailUrl(thumbnailPath.toUri().toString());
                                LOG.info("Thumbnail file name: {}", thumbnailFileName); // Log the filename
                            } else {
                                LOG.warn("Thumbnail file not found for video: {}", file.getFileName().toString());
                            }

                            // Set uploader
                            String uploaderName = rootNode.path("uploader").asText();
                            User uploader = userRepository.findByUsername(uploaderName);
                            if (uploader == null) {
                                uploader = new User();
                                uploader.setUsername(uploaderName);
                                uploader.setEmail(uploaderName + "@example.com");
                                uploader.setPassword(generateRandomPassword());
                                userRepository.save(uploader);
                            }
                            video.setUploader(uploader);

                            // Process categories
                            List<Category> categories = new ArrayList<>();
                            JsonNode categoriesNode = rootNode.path("meta").path("categories");
                            if (categoriesNode.isArray()) {
                                for (JsonNode categoryNode : categoriesNode) {
                                    String categoryName = categoryNode.asText();
                                    Category category = categoryRepository.findByName(categoryName);
                                    if (category == null) {
                                        category = new Category();
                                        category.setName(categoryName);
                                        categoryRepository.save(category);
                                    }
                                    categories.add(category);
                                }
                            }
                            video.setCategories(categories);

                            // Process tags
                            List<Tag> tags = new ArrayList<>();
                            JsonNode tagsNode = rootNode.path("meta").path("tags");
                            if (tagsNode.isArray()) {
                                for (JsonNode tagNode : tagsNode) {
                                    String tagName = tagNode.asText();
                                    Tag tag = tagRepository.findByName(tagName);
                                    if (tag == null) {
                                        tag = new Tag();
                                        tag.setName(tagName);
                                        tagRepository.save(tag);
                                    }
                                    tags.add(tag);
                                }
                            }
                            video.setTags(tags);

                            // Save video before processing comments
                            videoRepository.save(video);

                            // Process comments and users
                            List<Comment> comments = new ArrayList<>();
                            JsonNode commentsNode = rootNode.path("meta").path("comments");
                            if (commentsNode.isArray()) {
                                for (JsonNode commentNode : commentsNode) {
                                    String authorName = commentNode.path("author").asText();
                                    User user = userRepository.findByUsername(authorName);
                                    if (user == null) {
                                        user = new User();
                                        user.setUsername(authorName);
                                        user.setEmail(authorName + "@example.com");
                                        user.setPassword(generateRandomPassword());
                                        userRepository.save(user);
                                    }
                                    Comment comment = new Comment();
                                    comment.setContent(commentNode.path("text").asText());
                                    comment.setAuthor(user);
                                    comment.setVideo(video);
                                    comment.setTimestamp(LocalDateTime.now());
                                    commentRepository.save(comment);
                                    comments.add(comment);
                                }
                            }
                            video.setComments(comments);

                        } catch (IOException e) {
                            LOG.error("Error parsing JSON file: {}", e.getMessage());
                        }
                    });
            LOG.info("Loaded {} videos", videoList.size());
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