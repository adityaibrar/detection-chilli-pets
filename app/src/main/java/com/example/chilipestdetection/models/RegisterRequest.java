package com.example.chilipestdetection.models;

public class RegisterRequest {
    private String action;
    private String username;
    private String password;
    private String type_user;

    public RegisterRequest(String username, String password, String typeUser) {
        this.action = "register";
        this.username = username;
        this.password = password;
        this.type_user = typeUser;
    }

    // Getters and setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getType_user() { return type_user; }
    public void setType_user(String type_user) { this.type_user = type_user; }
}
