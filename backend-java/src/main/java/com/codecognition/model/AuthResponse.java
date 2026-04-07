package com.codecognition.model;

public class AuthResponse {
    public String token;
    public String email;
    public String fullName;
    public String role;
    public Long userId;

    public AuthResponse(String token, String email, String fullName, String role, Long userId) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.userId = userId;
    }
}
