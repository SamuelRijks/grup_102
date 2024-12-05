package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.dto.CommentResponseDTO;
import com.tecnocampus.LS2.protube_back.dto.UserDTO;
import com.tecnocampus.LS2.protube_back.dto.VideoSummaryDTO;
import com.tecnocampus.LS2.protube_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
    }


    @GetMapping("/{username}/comments")
    public List<CommentResponseDTO> getUserComments(@PathVariable String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Comments for user " + username + ": " + user.getComments());

        List<CommentResponseDTO> comments = user.getComments().stream()
                .map(comment -> new CommentResponseDTO(comment))
                .collect(Collectors.toList());

        return comments;
    }

    @GetMapping("/{username}/videos")
    public List<VideoSummaryDTO> getUserVideos(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<VideoSummaryDTO> videos = user.getVideos().stream()
                .map(video -> new VideoSummaryDTO(video.getId(), video.getTitle(), video.getUploader().getUsername(), video.getThumbnailUrl()))
                .collect(Collectors.toList());

        System.out.println("Videos for user " + username + ": " + videos);
        return videos;
    }
}