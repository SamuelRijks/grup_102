package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.AppStartupRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final AppStartupRunner appStartupRunner;

    @Autowired
    public VideoController(AppStartupRunner appStartupRunner) {
        this.appStartupRunner = appStartupRunner;
    }

    @GetMapping
    public List<String> getAllVideos() {
        return appStartupRunner.getVideoList();
    }
}
