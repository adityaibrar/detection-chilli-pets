package com.example.chilipestdetection.models;

public class LoginResponse {
    private boolean success;
    private String type_user;
    private String message;

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getType_user() { return type_user; }
    public void setType_user(String type_user) { this.type_user = type_user; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
