package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.configuration.JwtService;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        LOG.info("Attempting to log in user with username: {}", user.getUsername());
        try {
            Optional<User> optionalUser = userService.findByUsername(user.getUsername());
            LOG.info("User found: {}", optionalUser.isPresent());
            if (optionalUser.isPresent()) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtService.generateToken(user.getUsername());
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
            }
        } catch (Exception ex) {
            LOG.error("Login failed for user {}: {}", user.getUsername(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }
}