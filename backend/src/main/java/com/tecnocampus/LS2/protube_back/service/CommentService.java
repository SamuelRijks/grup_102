package com.tecnocampus.LS2.protube_back.service;

import com.tecnocampus.LS2.protube_back.domain.Comment;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.domain.Video;
import com.tecnocampus.LS2.protube_back.dto.CommentDTO;
import com.tecnocampus.LS2.protube_back.repository.CommentRepository;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public Comment createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();

        User user = userRepository.findById(commentDTO.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Video video = videoRepository.findById(commentDTO.getVideoId()).orElseThrow(() -> new RuntimeException("Video not found"));

        comment.setAuthor(user);
        comment.setVideo(video);
        comment.setContent(commentDTO.getContent());

        return commentRepository.save(comment);
    }
}