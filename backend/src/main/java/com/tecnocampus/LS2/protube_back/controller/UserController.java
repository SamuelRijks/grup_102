package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.domain.LoginRequest;
import com.tecnocampus.LS2.protube_back.domain.RegisterRequest;
import com.tecnocampus.LS2.protube_back.domain.User;
import com.tecnocampus.LS2.protube_back.service.AuthService;
import com.tecnocampus.LS2.protube_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired  
    private UserService userService;

    @Autowired
    private AuthService authService;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok("Usuario registrado con éxito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    // Endpoint para iniciar sesión y obtener el JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login fallido: " + e.getMessage());
        }
    }
}

