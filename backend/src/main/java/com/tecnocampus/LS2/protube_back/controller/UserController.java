package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}/comments")
    public List<Comment> getUserComments(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getComments();
    }

    @GetMapping("/{username}/videos")
    public List<Video> getUserVideos(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getVideos();
    }
}