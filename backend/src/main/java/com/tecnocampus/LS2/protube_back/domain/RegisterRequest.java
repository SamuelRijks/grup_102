package com.tecnocampus.LS2.protube_back.domain;

//DTO
public class RegisterRequest {
    private String email;
    private String password;

    // Getters y setters

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

