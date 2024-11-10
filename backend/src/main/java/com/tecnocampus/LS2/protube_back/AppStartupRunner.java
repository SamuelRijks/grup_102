package com.tecnocampus.LS2.protube_back;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger LOG =
            LoggerFactory.getLogger(AppStartupRunner.class);

    // Example variables from our implementation.
    // Feel free to adapt them to your needs
    private final Path rootPath;
    private final Boolean loadInitialData;
    private List<String> videoList;

    public AppStartupRunner(Environment env) {
        String storeDir = env.getProperty("pro_tube.store.dir");
        this.rootPath = Paths.get(storeDir);
        this.loadInitialData = env.getProperty("pro_tube.load_initial_data", Boolean.class);
        this.videoList = new ArrayList<>();
    }

    public List<String> getVideoList() {
        return videoList;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Should your backend perform any task during the bootstrap, do it here
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
                    .forEach(file -> {
                        LOG.info("Found video file: {}", file.getFileName().toString());
                        videoList.add(file.getFileName().toString());
                    });
            LOG.info("Loaded {} videos", videoList.size());
        } catch (IOException e) {
            LOG.error("Error loading videos: {}", e.getMessage());
        }
    }
}
