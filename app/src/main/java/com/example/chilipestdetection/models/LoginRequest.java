package com.example.chilipestdetection.models;

public class LoginRequest {
    private String action;
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.action = "login";
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
