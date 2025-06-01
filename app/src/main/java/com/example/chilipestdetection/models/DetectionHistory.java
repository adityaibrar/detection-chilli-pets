package com.example.chilipestdetection.models;

public class DetectionHistory {
    private int id;
    private String imagePath;
    private String detectionDate;
    private String pestType;
    private String severity;
    private float accuracy;

    public DetectionHistory() {
    }

    public DetectionHistory(String imagePath, String detectionDate, String pestType,
                            String severity, float accuracy) {
        this.imagePath = imagePath;
        this.detectionDate = detectionDate;
        this.pestType = pestType;
        this.severity = severity;
        this.accuracy = accuracy;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDetectionDate() {
        return detectionDate;
    }

    public void setDetectionDate(String detectionDate) {
        this.detectionDate = detectionDate;
    }

    public String getPestType() {
        return pestType;
    }

    public void setPestType(String pestType) {
        this.pestType = pestType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}