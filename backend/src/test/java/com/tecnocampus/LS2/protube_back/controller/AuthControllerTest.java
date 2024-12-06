package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
/*
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userService.registerUser(any(User.class))).thenReturn(user);

        User registeredUser = authController.registerUser(user);

        assertEquals("testuser", registeredUser.getUsername());
    }

    @Test
    public void testLoginUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password"));

        ResponseEntity<String> response = authController.loginUser(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    public void testLoginUser_Failure() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.loginUser(user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Login failed: User not found", response.getBody());
    }
}*/